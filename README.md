# Android-Testing
TDD-developed sample application for demonstration purposes. Developed with [android studio](https://developer.android.com/studio). Backed with data provided by [json-generator.com](https://www.json-generator.com).

## Unit Tests
- `ApiClientTest`: Unit test asserting correct fetching of data from JSonGenerator
- `TransactionsCalculatorTest`: Unit test asserting correct calculation of balances from multiple transactions.
- `EntryWithTintableValueFromTransactionsTest`: Unit test asserting correct transformation of data provided by `ApiClient` to data required by UI. 

## Integration Tests
- `TransactionsViewModelTest`, `TransactionSumsViewModelTest`: JVM Integration Test for testing a `ViewModel` and the backing `ApiClient`, checking values in the VM's `LiveData`, mocking server calls with `MockWebServer`
- `TransactionsFragmentIntegrationTest.kt`, `TransactionSumsFragmentIntegrationTest`: Android Instrumentation UI Test using Espresso, mocking Server Calls with `MockWebServer`
