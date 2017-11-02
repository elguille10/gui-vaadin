
package org.guivaadin.gui;


import com.vaadin.navigator.View;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Label;
import com.vaadin.navigator.ViewChangeListener;


public class ErrorView extends VerticalLayout implements View
{

	private Label explanation;

    public ErrorView()
    {
        setMargin(true);
        setSpacing(true);

        Label header = new Label("The view could not be found");
        addComponent(header);
        addComponent(explanation = new Label());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event)
    {
        explanation.setValue(String.format(
                "You tried to navigate to a view ('%s') that does not exist.",
                event.getViewName()));
    }
}
