// File managed by WebFX (DO NOT EDIT MANUALLY)

module modality.ecommerce.backoffice.operations.moneyflow {

    // Direct dependencies modules
    requires java.base;
    requires javafx.graphics;
    requires modality.base.shared.entities;
    requires webfx.framework.client.controls;
    requires webfx.framework.client.orm.entity.controls;
    requires webfx.framework.shared.operation;
    requires webfx.framework.shared.orm.entity;
    requires webfx.stack.async;
    requires webfx.stack.db.submit;

    // Exported packages
    exports org.modality_project.ecommerce.backoffice.operations.entities.moneyaccount;
    exports org.modality_project.ecommerce.backoffice.operations.entities.moneyflow;

}