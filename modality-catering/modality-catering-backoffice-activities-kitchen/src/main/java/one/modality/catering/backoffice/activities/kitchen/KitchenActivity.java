package one.modality.catering.backoffice.activities.kitchen;

import dev.webfx.stack.db.query.QueryArgument;
import dev.webfx.stack.db.query.QueryArgumentBuilder;
import dev.webfx.stack.db.query.QueryService;
import dev.webfx.stack.orm.domainmodel.activity.viewdomain.impl.ViewDomainActivityBase;
import dev.webfx.stack.orm.domainmodel.activity.viewdomain.impl.ViewDomainActivityContextFinal;
import dev.webfx.stack.orm.entity.EntityStore;
import dev.webfx.stack.routing.uirouter.activity.uiroute.UiRouteActivityContextMixin;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import one.modality.base.client.activity.ModalityButtonFactoryMixin;
import one.modality.base.shared.entities.Item;
import one.modality.base.shared.entities.Organization;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Bruno Salmon
 */
public class KitchenActivity extends ViewDomainActivityBase
        implements UiRouteActivityContextMixin<ViewDomainActivityContextFinal>,
        ModalityButtonFactoryMixin  {

    private static final String MEAL_COUNT_SQL = "select si.date, i.name, di.code, count(*)\n" +
            "from attendance a\n" +
            "  join scheduled_item si on si.id = a.scheduled_item_id\n" +
            "  join document_line dl on dl.id=a.document_line_id\n" +
            "  join site s on s.id=si.site_id\n" +
            "  join item i on i.id=si.item_id\n" +
            "  join item_family f on f.id=i.family_id  \n" +
            "  , ( select i.id,i.code,i.ord from item i join item_family f on f.id=i.family_id where i.organization_id = $1 and f.code='diet'\n" +
            "    union\n" +
            "    select * from (values (-1, 'Total', -1), (-2, '?', 10000)) vitem(id, code, ord)\n" +
            "    ) di\n" +
            "where not dl.cancelled\n" +
            "  and s.organization_id = $2\n" +
            "  and f.code = 'meals'\n" +
            "  and si.date between $3 and $4\n" +
            "  and case when di.id=-1 then true\n" +
            "       when di.id=-2 then not exists(select * from document_line dl2 join item i2 on i2.id=dl2.item_id join item_family f2 on f2.id=i2.family_id where dl2.document_id=dl.document_id and not dl2.cancelled and f2.code='diet')\n" +
            "       else exists(select * from document_line dl2 where dl2.document_id=dl.document_id and not dl2.cancelled and dl2.item_id=di.id)\n" +
            "        end\n" +
            "group by si.date, i.name, di.code, i.ord, di.ord\n" +
            "order by si.date, i.ord, di.ord;";

    private VBox body = new VBox();
    private ComboBox<Organization> organizationComboBox = new ComboBox<>();
    private MealsSelectionPane mealsSelectionPane = new MealsSelectionPane();
    private MonthSelectionPanel monthSelectionPanel = new MonthSelectionPanel(this::updateAttendanceMonthPanel);
    private AttendanceMonthPanel attendanceMonthPanel;
    private VBox attendanceCountsPanelContainer = new VBox();
    private AttendanceCounts attendanceCounts;

    public KitchenActivity() {
        mealsSelectionPane.selectedItemsProperty().addListener((observableValue, oldValue, newValue) -> {
            LocalDate selectedMonth = monthSelectionPanel.getSelectedMonth();
            refreshAttendanceMonthPanel(selectedMonth);
        });
    }

    @Override
    public Node buildUi() {
        organizationComboBox.setConverter(new StringConverter<Organization>() {
            @Override
            public String toString(Organization organization) {
                return organization != null ? organization.getName() : null;
            }

            @Override
            public Organization fromString(String s) {
                return null;
            }
        });
        organizationComboBox.valueProperty().addListener(new ChangeListener<Organization>() {
            @Override
            public void changed(ObservableValue<? extends Organization> observableValue, Organization oldValue, Organization newValue) {
                mealsSelectionPane.setOrganization(newValue);
                loadAttendance();
            }
        });
        body.getChildren().addAll(organizationComboBox, mealsSelectionPane, monthSelectionPanel, attendanceCountsPanelContainer);
        return body;
    }

    private void loadAttendance() {
        Organization organization = organizationComboBox.getValue();
        attendanceCounts = new AttendanceCounts();
        if (organization == null || organization.getId() == null) {
            return;
        }
        Object organizationId = organization.getId().getPrimaryKey();
        LocalDate selectedMonth = monthSelectionPanel.getSelectedMonth();
        LocalDate startDate = LocalDate.of(selectedMonth.getYear(), selectedMonth.getMonth(), 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        QueryArgument query = new QueryArgumentBuilder().setStatement(MEAL_COUNT_SQL)
                .setDataSourceId(getDataSourceModel().getDataSourceId())
                .setParameters(organizationId, organizationId, startDate, endDate)
                .build();

        QueryService.executeQuery(query)
                .onFailure(System.out::println)
                .onSuccess(result -> {
                    for (int row = 0; row < result.getRowCount(); row++) {
                        String dateString = result.getValue(row, 0);
                        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ISO_ZONED_DATE_TIME);
                        String meal = result.getValue(row, 1);
                        String dietaryOption = result.getValue(row, 2);
                        int count = result.getValue(row, 3);
                        attendanceCounts.add(date, meal, dietaryOption, count);
                    }
                    Platform.runLater(() -> refreshAttendanceMonthPanel(selectedMonth));
                });
    }

    private void updateAttendanceMonthPanel(LocalDate month) {
        loadAttendance();
        refreshAttendanceMonthPanel(month);
    }

    private void refreshAttendanceMonthPanel(LocalDate month) {
        List<Item> displayedMeals = mealsSelectionPane.selectedItemsProperty().get();
        attendanceMonthPanel = new AttendanceMonthPanel(attendanceCounts, month, displayedMeals);
        Platform.runLater(() -> attendanceCountsPanelContainer.getChildren().setAll(attendanceMonthPanel));
    }

    @Override
    protected void startLogic() {
        // Populate organization combo box
        EntityStore.create(getDataSourceModel())
                .executeQuery("select name from Organization where !closed and name!=`ISC`")
                .onSuccess(organizations -> {
                    List<Organization> organizationList = organizations.stream()
                            .map(entity -> (Organization) entity)
                            .sorted((org1, org2) -> org1.getName().compareTo(org2.getName()))
                            .collect(Collectors.toList());
                    organizationComboBox.setItems(FXCollections.observableArrayList(organizationList));
                });
    }

}
