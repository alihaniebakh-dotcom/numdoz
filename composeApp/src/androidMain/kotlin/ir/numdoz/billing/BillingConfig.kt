package ir.numdoz.billing

object BillingConfig {
    // RSA Public Key from Bazaar developer panel (In-App Billing page)
    const val BAZAAR_RSA_KEY = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwCTkaZQaZkvYsJU797QOz7hWmS8vc+OJdK9q4/g4TO8rGBbfMRgUk3kLqV7H0CD2EUTKGDgSYI7BKduUzOSJH3sX6ibgVvzPSSWhqeWXKaOTd/kCFfXZnIboMIFni/m2/Os2mX+KGcFYl768zSykYk8YEveLjVbp4OPeJuNWSWEVrN1vFp2GEkDj6ryg6V2/k6+PppD/VsjsSkhzZ60a6RJKXLnKyEQpeAeTH9li3kCAwEAAQ=="

    // Must match exactly the product SKUs created in Bazaar panel.
    val PRODUCT_IDS = listOf("coin_100", "coin_500", "coin_1000")
}