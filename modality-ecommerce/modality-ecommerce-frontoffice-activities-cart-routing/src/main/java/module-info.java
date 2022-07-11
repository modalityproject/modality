// File managed by WebFX (DO NOT EDIT MANUALLY)

module modality.ecommerce.frontoffice.activities.cart.routing {

    // Direct dependencies modules
    requires java.base;
    requires javafx.base;
    requires javafx.graphics;
    requires modality.base.client.activity;
    requires modality.base.client.aggregates;
    requires modality.base.client.util;
    requires modality.base.shared.entities;
    requires webfx.framework.client.i18n;
    requires webfx.framework.client.orm.domainmodel.activity;
    requires webfx.framework.client.uirouter;
    requires webfx.framework.client.util;
    requires webfx.kit.util;
    requires webfx.platform.shared.util;
    requires webfx.stack.async;
    requires webfx.stack.platform.json;
    requires webfx.stack.platform.windowhistory;

    // Exported packages
    exports org.modality_project.ecommerce.frontoffice.activities.cart.base;
    exports org.modality_project.ecommerce.frontoffice.activities.cart.routing;
    exports org.modality_project.ecommerce.frontoffice.operations.cart;

}