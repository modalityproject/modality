package one.modality.event.frontoffice.activities.booking.views;

import dev.webfx.extras.panes.AspectRatioPane;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import one.modality.base.client.brand.Brand;
import one.modality.base.client.css.Fonts;
import one.modality.base.frontoffice.utility.tyler.GeneralUtility;
import one.modality.base.frontoffice.utility.tyler.StyleUtility;
import one.modality.base.frontoffice.utility.tyler.TextUtility;

/**
 * @author Bruno Salmon
 */
final class OrangeFrame {

    public static VBox createOrangeFrame(String headerI18nKey, Node center, Node bottom) {
        AspectRatioPane aspectRatioPane = new AspectRatioPane(16d / 9, center);
        Text headerText = TextUtility.createText(headerI18nKey, Color.WHITE);
        VBox orangeFrame = new VBox(20,
                headerText,
            aspectRatioPane,
                bottom
        );

        orangeFrame.setBackground(Background.fill(Brand.getBrandMainColor()));
        orangeFrame.setAlignment(Pos.CENTER);

        FXProperties.runOnDoublePropertyChange(width -> {
            double fontFactor = GeneralUtility.computeFontFactor(width);
            TextUtility.setTextFont(headerText, Fonts.MONTSERRAT_TEXT_FAMILY, FontWeight.BOLD, fontFactor * StyleUtility.MAIN_TEXT_SIZE);
            if (bottom instanceof Labeled)
                GeneralUtility.setLabeledFont((Labeled) bottom, Fonts.MONTSERRAT_TEXT_FAMILY, FontWeight.BOLD, fontFactor * StyleUtility.MAIN_TEXT_SIZE);
            double space = Math.min(35, width * 0.03);
            orangeFrame.setSpacing(space);
            orangeFrame.setPadding(new Insets(space));
            double screenHeight = Screen.getPrimary().getBounds().getHeight();
            aspectRatioPane.setAspectRatio(Math.max(1.5, width / (screenHeight * 0.6)));
        }, orangeFrame.widthProperty());

        return orangeFrame;
    }

}
