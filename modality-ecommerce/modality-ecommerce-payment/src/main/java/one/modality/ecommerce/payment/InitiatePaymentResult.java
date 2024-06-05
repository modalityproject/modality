package one.modality.ecommerce.payment;

/**
 * @author Bruno Salmon
 */
public final class InitiatePaymentResult {

    private final Object paymentPrimaryKey; // PK of the generated payment in the database (MoneyTransfer in Modality)
    private final String htmlContent; // Direct HTML content that can handle the payment (CC details, etc...) in an embedded WebView (ex: Stripe)
    private final String url; // URL of the page that can handle the payment (redirect will tell what to do with it)
    private final boolean redirect; // true => URL needs to be opened in a separate browser window, false => URL can be opened in an embedded WebView (ex: Square)

    public InitiatePaymentResult(Object paymentPrimaryKey, String htmlContent) {
        this(paymentPrimaryKey, htmlContent, null, false);
    }

    public InitiatePaymentResult(Object paymentPrimaryKey, String url, boolean redirect) {
        this(paymentPrimaryKey, null, url, redirect);
    }

    public InitiatePaymentResult(Object paymentPrimaryKey, String htmlContent, String url, boolean redirect) {
        this.paymentPrimaryKey = paymentPrimaryKey;
        this.htmlContent = htmlContent;
        this.url = url;
        this.redirect = redirect;
    }

    public Object getPaymentPrimaryKey() {
        return paymentPrimaryKey;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public String getUrl() {
        return url;
    }

    public boolean isRedirect() {
        return redirect;
    }

}
