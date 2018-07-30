package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.util.Locale;
import java.util.ResourceBundle;

public class LabeledTextFieldTest extends ApplicationTest {

  private LabeledTextField labeledTextField;
  private final Locale locale = new Locale("en");
  private final ResourceBundle bundle = ResourceBundle.getBundle("lang.main", locale);


  @Test
  public void testLabeledTextField() {
    final Label label = (Label) labeledTextField.lookup("#label");
    Assert.assertNull(label.getText());
    Assert.assertTrue(label.isVisible());
    labeledTextField.setLabelText("test");
    Assert.assertTrue(labeledTextField.textProperty().isEmpty().get());

    final TextField textField = (TextField) labeledTextField.lookup("#textField");
    Assert.assertFalse(textField.isDisable());
    Assert.assertFalse(labeledTextField.disableTextFieldProperty().get());
    labeledTextField.textProperty().set("test text");
    Assert.assertEquals("test text", textField.getText());
    labeledTextField.disableTextFieldProperty().set(true);
    Assert.assertTrue(textField.isDisable());
    labeledTextField.disableTextFieldProperty().set(false);
    Assert.assertFalse(textField.isDisable());
  }

  @Override
  public void start(final Stage stage) {
    final Inflater inflater = new Inflater(new FXMLLoader(),bundle);
    labeledTextField = new LabeledTextField(inflater);

    final Scene scene = new Scene(labeledTextField, 100, 100);

    stage.setScene(scene);
    stage.show();
  }
}
