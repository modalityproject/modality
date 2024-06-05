package one.modality.ecommerce.payment.gateway;

/**
 * @author Bruno Salmon
 */
public class GatewayMakeApiPaymentResult {

    private final boolean success;

    public GatewayMakeApiPaymentResult(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

}
