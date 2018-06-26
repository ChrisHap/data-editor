package de.hhu.stups.plues.dataeditor.ui.components;

import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * A {@link VBox} containing a {@link Label} and a {@link TextField}. The input text is exposed by
 * {@link #textProperty()}.
 */
@Component
@Scope("prototype")
public class LabeledTextField extends VBox implements Initializable {

  private StringProperty labelTextProperty;

  @FXML
  @SuppressWarnings("unused")
  private Label label;
  @FXML
  @SuppressWarnings("unused")
  private TextField textField;

  @Autowired
  public LabeledTextField(final Inflater inflater) {
    labelTextProperty = new SimpleStringProperty();
    inflater.inflate("components/labeled_text_field", this, this);
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    if (label != null) {
      label.textProperty().bind(labelTextProperty);
    }
  }

  public void setLabelText(String labelText) {
    Platform.runLater(() -> this.labelTextProperty.set(labelText));
  }

  /**
   * Return the {@link #textField}'s text property.
   */
  public StringProperty textProperty() {
    return textField.textProperty();
  }

  public void setText(final String text) {
    textField.setText(text);
  }

  public BooleanProperty disableTextFieldProperty() {
    return textField.disableProperty();
  }

  @SuppressWarnings("unused")
  public void clear() {
    textField.clear();
  }
}
