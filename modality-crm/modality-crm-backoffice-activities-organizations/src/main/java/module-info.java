// File managed by WebFX (DO NOT EDIT MANUALLY)

module modality.crm.backoffice.activities.organizations {

    // Direct dependencies modules
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires modality.base.client.activity;
    requires modality.event.backoffice.activities.events;
    requires webfx.framework.client.activity;
    requires webfx.framework.client.i18n;
    requires webfx.framework.client.orm.domainmodel.activity;
    requires webfx.framework.client.orm.reactive.visual;
    requires webfx.framework.client.uirouter;
    requires webfx.framework.shared.operation;
    requires webfx.framework.shared.orm.dql;
    requires webfx.framework.shared.orm.entity;
    requires webfx.framework.shared.router;
    requires webfx.platform.shared.util;
    requires webfx.stack.async;
    requires webfx.stack.platform.windowhistory;

    // Exported packages
    exports org.modality_project.crm.backoffice.activities.organizations;
    exports org.modality_project.crm.backoffice.activities.organizations.routing;
    exports org.modality_project.crm.backoffice.operations.routes.organizations;

    // Provided services
    provides dev.webfx.stack.framework.client.operations.route.RouteRequestEmitter with org.modality_project.crm.backoffice.activities.organizations.RouteToOrganizationsRequestEmitter;
    provides dev.webfx.stack.framework.client.ui.uirouter.UiRoute with org.modality_project.crm.backoffice.activities.organizations.OrganizationsUiRoute;

}