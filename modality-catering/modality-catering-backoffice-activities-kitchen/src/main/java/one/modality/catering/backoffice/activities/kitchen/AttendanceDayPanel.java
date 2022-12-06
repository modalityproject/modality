package one.modality.catering.backoffice.activities.kitchen;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.List;

public class AttendanceDayPanel extends GridPane {

    public AttendanceDayPanel(AttendanceCounts attendanceCounts, LocalDate date) {
        addDayOfMonthNumber(date);
        addMealTitles(attendanceCounts);
        addMealCounts(attendanceCounts, date);
    }

    private void addDayOfMonthNumber(LocalDate date) {
        int dayNumber = date.getDayOfMonth();
        Label dayNumberLabel = new Label(String.valueOf(dayNumber));
        add(dayNumberLabel, 0, 0);
    }

    private void addMealTitles(AttendanceCounts attendanceCounts) {
        List<String> titles = attendanceCounts.getSortedKeys();
        int titleColumnIndex = 1;
        for (String title : titles) {
            Label titleLabel = new Label(title);
            add(titleLabel, titleColumnIndex, 0);
            titleColumnIndex++;
        }
    }

    private void addMealCounts(AttendanceCounts attendanceCounts, LocalDate date) {
        List<String> titles = attendanceCounts.getSortedKeys();
        int countColumnIndex = 1;
        for (String title : titles) {
            int count = attendanceCounts.getCount(date, title);
            Label countLabel = new Label(String.valueOf(count));
            add(countLabel, countColumnIndex, 1);
            countColumnIndex++;
        }
    }

}
