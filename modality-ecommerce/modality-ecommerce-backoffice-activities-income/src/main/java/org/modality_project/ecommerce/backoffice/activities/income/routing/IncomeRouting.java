package org.modality_project.ecommerce.backoffice.activities.income.routing;

import org.modality_project.base.client.util.routing.ModalityRoutingUtil;

/**
 * @author Bruno Salmon
 */
public final class IncomeRouting {

    private final static String PATH = "/income/event/:eventId";

    public static String getPath() {
        return PATH;
    }

    public static String getEventIncomePath(Object eventId) {
        return ModalityRoutingUtil.interpolateEventIdInPath(eventId, PATH);
    }

}
