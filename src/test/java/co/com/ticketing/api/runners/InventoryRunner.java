package co.com.ticketing.api.runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features/inventory/inventory_crud.feature",
        glue = "co.com.ticketing.api.stepdefinitions",
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        tags = "@InventoryCRUD"
)
public class InventoryRunner {
}
