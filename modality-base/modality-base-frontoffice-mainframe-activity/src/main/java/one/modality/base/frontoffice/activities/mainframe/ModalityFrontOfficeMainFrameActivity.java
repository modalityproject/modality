package one.modality.base.frontoffice.activities.mainframe;

import dev.webfx.extras.panes.*;
import dev.webfx.extras.panes.transitions.CircleTransition;
import dev.webfx.extras.panes.transitions.FadeTransition;
import dev.webfx.extras.player.Players;
import dev.webfx.extras.util.control.ControlUtil;
import dev.webfx.extras.util.layout.LayoutUtil;
import dev.webfx.kit.launcher.WebFxKitLauncher;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.kit.util.properties.ObservableLists;
import dev.webfx.platform.conf.Config;
import dev.webfx.platform.conf.SourcesConfig;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.useragent.UserAgent;
import dev.webfx.platform.util.Arrays;
import dev.webfx.platform.util.collection.Collections;
import dev.webfx.stack.i18n.controls.I18nControls;
import dev.webfx.stack.i18n.operations.ChangeLanguageRequestEmitter;
import dev.webfx.stack.routing.uirouter.UiRouter;
import dev.webfx.stack.session.state.client.fx.FXLoggedIn;
import dev.webfx.stack.ui.action.Action;
import dev.webfx.stack.ui.action.ActionBinder;
import dev.webfx.stack.ui.action.ActionGroup;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import one.modality.base.client.application.ModalityClientMainFrameActivity;
import one.modality.base.client.application.RoutingActions;
import one.modality.base.client.brand.Brand;
import one.modality.base.client.brand.BrandI18nKeys;
import one.modality.base.client.mainframe.fx.FXMainFrameDialogArea;
import one.modality.base.client.mainframe.fx.FXMainFrameOverlayArea;
import one.modality.base.client.mainframe.fx.FXMainFrameTransiting;
import one.modality.base.frontoffice.mainframe.fx.FXBackgroundNode;
import one.modality.base.frontoffice.mainframe.fx.FXCollapseFooter;
import one.modality.base.frontoffice.utility.page.FOPageUtil;
import one.modality.crm.shared.services.authn.fx.FXUserName;

import java.util.List;
import java.util.Objects;

public final class ModalityFrontOfficeMainFrameActivity extends ModalityClientMainFrameActivity {

    private static final double WEB_MAIN_MENU_HEIGHT = 100;
    private static final double WEB_USER_MENU_HEIGHT = 52;

    private final static Config FRONT_OFFICE_CONFIG = SourcesConfig.getSourcesRootConfig().childConfigAt("modality.base.frontoffice.application");
    private final static String[] MAIN_MENU_OPERATION_CODES = FRONT_OFFICE_CONFIG.getString("mainMenuOperationCodes").split(",");
    private final static String[] USER_MENU_OPERATION_CODES = FRONT_OFFICE_CONFIG.getString("userMenuOperationCodes").split(",");

    private final BooleanProperty mobileLayoutProperty =
        FXProperties.newBooleanProperty(UserAgent.isNative(), this::onMobileLayoutChange);

    private Pane mainFrameContainer;
    private Node backgroundNode; // can be used to hold a WebView, and prevent iFrame reload in the web version
    private final TransitionPane mountTransitionPane = new TransitionPane();
    private CollapsePane overlayMainMenuBar;
    private CollapsePane mountMainMenuButtonBar;
    private CollapsePane mobileMenuBar;
    private ScalePane[] scaledMobileButtons;
    private Pane dialogArea;
    private int firstOverlayChildIndex;

    @Override
    public Node buildUi() {
        // Starting with a circle transition animation for the first activity displayed
        mountTransitionPane.setAnimateFirstContent(true);
        mountTransitionPane.setTransition(new CircleTransition());
        // And then a fade transition for subsequent activities
        FXProperties.runOnPropertyChange(transiting -> {
            if (transiting) {
                overlayMainMenuBar.setAnimate(false);
            } else { // Ending a transition
                mountTransitionPane.setTransition(new FadeTransition());
            }
        }, mountTransitionPane.transitingProperty());
        //mountTransitionPane.setKeepsLeavingNodes(true); // Note: activities with video players should call TransitionPane.setKeepsLeavingNode(node, false)
        FXMainFrameTransiting.transitingProperty().bind(mountTransitionPane.transitingProperty());
        mainFrameContainer = new LayoutPane() { // Children are set later in updateMountNode()
            @Override
            protected void layoutChildren(double width, double height) {
                double headerHeight = 0, footerHeight = 0;
                boolean isMobileLayout = mobileLayoutProperty.get();
                if (isMobileLayout) {
                    footerHeight = mobileMenuBar.prefHeight(width);
                    layoutInArea(mobileMenuBar, 0, height - footerHeight, width, footerHeight);
                } else if (mountMainMenuButtonBar != null) {
                    Point2D p = mountMainMenuButtonBar.localToScene(0, 0);
                    layoutInArea(overlayMainMenuBar, p.getX(), 0, mountMainMenuButtonBar.getWidth(), mountMainMenuButtonBar.getHeight());
                }
                double mountNodeY = headerHeight;
                double mountNodeHeight = height - headerHeight - footerHeight;
                mountTransitionPane.setMinHeight(mountNodeHeight);
                layoutInArea(mountTransitionPane, 0, mountNodeY, width, mountNodeHeight);
                if (backgroundNode != null) { // Same position & size as the mount node (if present)
                    layoutInArea(backgroundNode, 0, mountNodeY, width, mountNodeHeight);
                }
                if (dialogArea != null) { // Same position & size as the mount node (if present)
                    layoutInArea(dialogArea, 0, mountNodeY, width, mountNodeHeight);
                }
            }
        };
        mobileMenuBar = createMainMenuButtonBar(true);
        overlayMainMenuBar = createMainMenuButtonBar(false);
        overlayMainMenuBar.setAnimate(false);
        overlayMainMenuBar.collapse();
        overlayMainMenuBar.setVisible(false);
        double[] lastMouseY = { 0 };
        mainFrameContainer.setOnMouseMoved(e -> {
            double mouseY = e.getY(), mouseX = e.getSceneX();
            if (Math.abs(mouseY - lastMouseY[0]) > 5 && !FXCollapseFooter.isCollapseFooter()) {
                Node mountNode = getMountNode();
                ScrollPane scrollPane = mountNode == null ? null : (ScrollPane) mountNode.getProperties().get("embedding-scrollpane");
                boolean isPageOnTop = scrollPane == null || scrollPane.getVvalue() == 0;
                boolean up = mouseY < lastMouseY[0];
                if (!isPageOnTop && up && mouseY < mainFrameContainer.getHeight() / 3) {
                    Node node = overlayMainMenuBar.getContent(); // should return a mono pane;
                    if (node instanceof MonoPane)
                        node = ((MonoPane) node).getContent(); // should return a hbox
                    if (node instanceof Parent)
                        node = ((Parent) node).getChildrenUnmodifiable().get(2); // Should be first button
                    if (node != null && mouseX >= node.localToScene(0, 0).getX()) {
                        if (overlayMainMenuBar.isCollapsed()) {
                            overlayMainMenuBar.setAnimate(true);
                            overlayMainMenuBar.expand();
                        }
                    }
                } else if (!up && mouseY > WEB_MAIN_MENU_HEIGHT) {
                    overlayMainMenuBar.setAnimate(!isPageOnTop);
                    overlayMainMenuBar.collapse();
                }
            }
            lastMouseY[0] = mouseY;
        });

        // To be aware: if backgroundNode is set to a WebView (which is actually its main purpose), then modifying the
        // mainFrame children again will cause the iFrame to reload in the web version, which is what we want to prevent
        // here: when the user is navigating back to the WebView, we want him to retrieve the WebView in the exact same
        // state as when he left it. So we try to not modify these children anymore once the backgroundNode is set.
        // That's why we encapsulated the mount node inside a container that won't change in that list.
        FXProperties.runNowAndOnPropertiesChange(() -> {
            backgroundNode = FXBackgroundNode.getBackgroundNode();
            boolean isMobileLayout = mobileLayoutProperty.get();
            // Here are the children we need to set for the main frame container
            List<Node> children = Collections.listOfRemoveNulls(
                backgroundNode,      // may be a WebView
                mountTransitionPane, // contains a standard mount node, or null if we want to display the backgroundNode
                isMobileLayout ? mobileMenuBar : overlayMainMenuBar); // mobile menu bar (at bottom) or overlay web menu bar (in addition to the one inside mountTransitionPane)
            // We call setAll() only if they differ, because setAll() is basically a clear() + addAll() and this causes
            // unnecessary changes in the DOM which in addition cause iFrames to unload
            if (!Objects.equals(children, mainFrameContainer.getChildren()))
                mainFrameContainer.getChildren().setAll(children);
            firstOverlayChildIndex = mainFrameContainer.getChildren().size();
            updateOverlayChildren();
            if (getMountNode() == null)
                overlayMainMenuBar.collapse();
        }, FXBackgroundNode.backgroundNodeProperty(), mobileLayoutProperty, mountNodeProperty());

        // Reacting to the mount node changes:
        FXProperties.runNowAndOnPropertyChange(mountNode -> {
            // Updating the mount node container with the new mount node
            UiRouter uiRouter = getUiRouter();
            mountTransitionPane.setReverse(uiRouter.getHistory().isGoingBackward());
            ScrollPane scrollPane = mountNode == null ? null : (ScrollPane) mountNode.getProperties().get("embedding-scrollpane");
            if (scrollPane == null && mountNode != null) {
                CollapsePane mainMenuButtonBar = createMainMenuButtonBar(false);
                CollapsePane userMenuButtonBar = createUserMenuButtonBar();
                userMenuButtonBar.setAnimate(false);
                userMenuButtonBar.collapsedProperty().bind(FXLoggedIn.loggedInProperty().not());
                userMenuButtonBar.setAnimate(true);
                VBox vBox = new VBox(
                    mainMenuButtonBar,
                    userMenuButtonBar,
                    mountNode
                );
                vBox.setAlignment(Pos.CENTER);
                //vBox.setBackground(Background.fill(Color.PINK));
                vBox.setMaxWidth(Double.MAX_VALUE);
                if (mountNode instanceof Region) {
                    Region mountRegion = (Region) mountNode;
                    //mountRegion.setMaxWidth(FrontOfficeActivityUtil.MAX_PAGE_WIDTH);
                    FXProperties.runOnPropertiesChange(() -> {
                        mountRegion.setMinHeight(mountTransitionPane.getMinHeight() - mainMenuButtonBar.getHeight());
                    }, mountTransitionPane.minHeightProperty(), mainMenuButtonBar.heightProperty());
                }
                BorderPane borderPane = new BorderPane(vBox);
                ScrollPane finalScrollPane = scrollPane = ControlUtil.createVerticalScrollPane(borderPane);
                mountNode.getProperties().put("embedding-scrollpane", scrollPane);
                double[] lastScrollPaneContentHeight = { 0 };
                FXProperties.runOnPropertyChange((o, oldValue, newValue) -> {
                    mountMainMenuButtonBar = mainMenuButtonBar;
                    if (mountTransitionPane.isTransiting())
                        return;
                    // Visibility management:
                    if (oldValue.doubleValue() == 0 || newValue.doubleValue() == 0) { // Making the overlay web menu bar invisible when reaching the top
                        overlayMainMenuBar.setAnimate(false); // because there is already a web menu on top of that page
                        overlayMainMenuBar.collapse();
                    } else if (ControlUtil.computeScrollPaneVTopOffset(finalScrollPane) > finalScrollPane.getHeight()) {
                        overlayMainMenuBar.setVisible(true);
                        overlayMainMenuBar.setAnimate(true); // Making it visible when the top one is no more in the view
                    }
                    // port (however it will not be showing while it's collapsed)
                    // Collapse management:
                    // Collapsing the overlay web menu if an activity explicitly asked to do so
                    if (FXCollapseFooter.isCollapseFooter())
                        overlayMainMenuBar.collapse();
                    // or if the content change its height (ex: lazy loading) because the change of the scroll position
                    // (ex: new content added => scroll position goes up) is not coming from the user in that case.
                    else if (lastScrollPaneContentHeight[0] != borderPane.getHeight())
                        overlayMainMenuBar.collapse();
                    // otherwise (we assume the scroll position comes from the user) if the user is scrolling up
                    else {
                        overlayMainMenuBar.setCollapsed(newValue.doubleValue() > oldValue.doubleValue());
                    }
                    lastScrollPaneContentHeight[0] = borderPane.getHeight();
                }, scrollPane.vvalueProperty());
            }
            mountTransitionPane.transitToContent(scrollPane);
            // When the mount node is null, this is to indicate that we want to display the background node instead
            boolean displayBackgroundNode = mountNode == null;
            // We make the background node visible only when we want to display it
            if (backgroundNode != null)
                backgroundNode.setVisible(displayBackgroundNode);
            // Also when we display the background node, we need make the mount node container transparent to the mouse
            // (as the background node is behind) to allow the user to interact with it (ex: WebView).
            mountTransitionPane.setMouseTransparent(displayBackgroundNode);
            updateDialogArea();
        }, mountNodeProperty());

        FXMainFrameOverlayArea.setOverlayArea(mainFrameContainer);
        ObservableLists.runOnListChange(this::updateOverlayChildren, FXMainFrameOverlayArea.getOverlayChildren());

        // Requesting a layout for containerPane on layout mode changes
        FXProperties.runNowAndOnPropertiesChange(() -> {
            double footerHeight = Math.max(0.08 * (Math.min(mainFrameContainer.getHeight(), mainFrameContainer.getWidth())), 40);
            Arrays.forEach(scaledMobileButtons, scaledButton -> scaledButton.setPrefHeight(footerHeight));
        }, mainFrameContainer.widthProperty(), mainFrameContainer.heightProperty());

        setUpContextMenu(mainFrameContainer, this::contextMenuActionGroup);

        setupPlayersGlobalConfiguration();

        mainFrameContainer.setOnMouseClicked(e -> {
            if (e.isShiftDown())
                FXProperties.toggleProperty(mobileLayoutProperty);
        });

        mainFrameContainer.setBackground(Background.fill(Color.WHITE));
        return mainFrameContainer;
    }

    private static void setupPlayersGlobalConfiguration() {
        // Fullscreen button
        ModalityFullscreenButton.setupModalityFullscreenButton();
        // Players color (actually only Wistia supports it)
        Players.setGlobalPlayerColor(Brand.getBrandMainColor());
    }

    private void updateDialogArea() {
        if (dialogArea != null)
            mainFrameContainer.getChildren().remove(dialogArea);
        dialogArea = null;
        Node relatedDialogNode = getMountNode();
        if (relatedDialogNode != null) {
            var properties = relatedDialogNode.getProperties();
            String arbitraryKey = "modality-dialogArea";
            dialogArea = (Pane) properties.get(arbitraryKey);
            if (dialogArea == null) {
                properties.put(arbitraryKey, dialogArea = new Pane());
                // We request focus on mouse clicked. This is to allow dropdown dialog in ButtonSelector to automatically
                // close when the user clicks outside (this auto-close mechanism is triggered by fucus change).
                dialogArea.setOnMouseClicked(e -> dialogArea.requestFocus());
                // We automatically show or hide the dialog area, depending on the presence or not of children:
                dialogArea.getChildren().addListener((InvalidationListener) observable -> showHideDialogArea());
            } else
                showHideDialogArea();
        }
        FXMainFrameDialogArea.setDialogArea(dialogArea);
    }

    private void showHideDialogArea() {
        ObservableList<Node> mainFrameChildren = mainFrameContainer.getChildren();
        if (dialogArea.getChildren().isEmpty())
            mainFrameChildren.remove(dialogArea);
        else if (!mainFrameChildren.contains(dialogArea)) {
            mainFrameChildren.add(firstOverlayChildIndex - 1, dialogArea);
        }
    }

    private void onMobileLayoutChange(boolean mobileLayout) {
        Console.log("mobileLayout = " + mobileLayout);
    }

    private void updateOverlayChildren() {
        ObservableList<Node> mainFrameChildren = mainFrameContainer.getChildren();
        ObservableList<Node> overlayChildren = FXMainFrameOverlayArea.getOverlayChildren();
        while (firstOverlayChildIndex < mainFrameChildren.size())
            mainFrameChildren.remove(firstOverlayChildIndex);
        mainFrameChildren.addAll(firstOverlayChildIndex, overlayChildren);
    }

    private ActionGroup contextMenuActionGroup() {
        return newActionGroup(
            ChangeLanguageRequestEmitter.getProvidedEmitters().stream()
                .map(emitter -> newOperationAction(emitter::emitLanguageRequest))
                .toArray(Action[]::new)
        );
    }

    private CollapsePane createUserMenuButtonBar() {
        return createMenuButtonBar(USER_MENU_OPERATION_CODES, true, false);
    }

    private CollapsePane createMainMenuButtonBar(boolean mobileLayout) {
        return createMenuButtonBar(MAIN_MENU_OPERATION_CODES, false, mobileLayout);
    }

    private CollapsePane createMenuButtonBar(String[] menuOperationCodes, boolean userMenu, boolean mobileLayout) {
        Button[] buttons = RoutingActions.filterRoutingActions(this, this, menuOperationCodes)
            .stream().map(action -> createMenuButton(action, userMenu, mobileLayout))
            .toArray(Button[]::new);
        Region buttonBar;
        if (mobileLayout) {
            scaledMobileButtons = Arrays.map(buttons, ModalityFrontOfficeMainFrameActivity::scaleButton, ScalePane[]::new);
            buttonBar = new ColumnsPane(scaledMobileButtons);
        } else {
            HBox hBox = new HBox(23, buttons);
            FOPageUtil.restrictToMaxPageWidthAndApplyPageLeftRightPadding(hBox);  // to fit like the mount node
            if (userMenu) {
                Label usernameLabel = new Label();
                usernameLabel.textProperty().bind(FXUserName.userNameProperty());
                hBox.getChildren().add(0, usernameLabel);
                hBox.getChildren().add(1, LayoutUtil.createHGrowable());
                hBox.setAlignment(Pos.CENTER_RIGHT);
                buttonBar = hBox;
                buttonBar.setPrefHeight(WEB_USER_MENU_HEIGHT);
            } else {
                Label brandLabel = I18nControls.newLabel(BrandI18nKeys.frontOfficeBrandNameAndLogo);
                brandLabel.setGraphicTextGap(20);
                brandLabel.getStyleClass().setAll("brand");
                hBox.getChildren().add(0, brandLabel);
                hBox.getChildren().add(1, LayoutUtil.createHGrowable());
                hBox.setAlignment(Pos.BOTTOM_RIGHT);
                hBox.setMaxHeight(Region.USE_PREF_SIZE);
                buttonBar = new MonoPane(hBox);
                buttonBar.setMinHeight(WEB_MAIN_MENU_HEIGHT);
                buttonBar.setPrefHeight(WEB_MAIN_MENU_HEIGHT);
                buttonBar.setMaxHeight(WEB_MAIN_MENU_HEIGHT);
            }
        }
        buttonBar.getStyleClass().setAll("button-bar"); // to make buttons square in CSS (remove round corners)
        CollapsePane collapsePane = new CollapsePane(buttonBar);
        collapsePane.getStyleClass().setAll("menu-bar", userMenu ? "user-menu-bar" : "main-menu-bar", mobileLayout ? "mobile" : "non-mobile");
        collapsePane.setMaxWidth(Double.MAX_VALUE); // necessary to make the (CSS) border fill the whole page width
        if (mobileLayout) {
            collapsePane.setEffect(new DropShadow());
            collapsePane.setClipEnabled(false);
            collapsePane.collapsedProperty().bind(FXCollapseFooter.collapseFooterProperty());
            // Considering the bottom of the safe area, in particular for OS like iPadOS with a bar at the bottom
            FXProperties.runNowAndOnPropertyChange(sai -> {
                double safeAreaBottom = sai.getBottom();
                // we already have a 5px padding for the buttons
                collapsePane.setPadding(new Insets(0, 0, Math.max(0, safeAreaBottom - 5), 0));
            }, WebFxKitLauncher.safeAreaInsetsProperty());
        }
        return collapsePane;
    }

    private Button createMenuButton(Action routeAction, boolean userMenu, boolean mobileLayout) {
        Button button = ActionBinder.newActionButton(routeAction);
        button.setCursor(Cursor.HAND);
        button.setContentDisplay(userMenu ? ContentDisplay.LEFT : ContentDisplay.TOP);
        button.setGraphicTextGap(mobileLayout ? 0 : 8);
        button.setMinWidth(Region.USE_PREF_SIZE);
        FXProperties.runNowAndOnPropertyChange(graphic -> {
            if (graphic instanceof SVGPath) {
                SVGPath svgPath = (SVGPath) graphic;
                boolean hasStroke = svgPath.getStroke() != null;
                ObjectProperty<Paint> svgColorProperty = hasStroke ? svgPath.strokeProperty() : svgPath.fillProperty();
                if (mobileLayout) {
                    button.textFillProperty().bind(svgColorProperty);
                } else {
                    /* Commented as this was making the Books stroke to null (icon was appearing black)
                    svgColorProperty.bind(button.textFillProperty());
                    */
                    svgColorProperty.set(Color.BLACK); // menu items color hard-code for now
                }
            }
        }, button.graphicProperty());
        button.setPadding(mobileLayout ? new Insets(5) : Insets.EMPTY);
        return button;
    }

    private static ScalePane scaleButton(Button button) {
        ScalePane scalePane = new ScalePane(ScaleMode.FIT_HEIGHT, button);
        scalePane.setStretchWidth(true);
        scalePane.setStretchHeight(true);
        scalePane.visibleProperty().bind(button.visibleProperty());
        scalePane.managedProperty().bind(button.managedProperty()); // Should it be in MonoPane?
        return scalePane;
    }
}
