package org.fmalik.automation.v4.speedImprovements;

public class LogElement extends ElementDecorator {
    public LogElement(Element element) {
        super(element);
    }

    @Override
    public String getText(){
        System.out.println(String.format("Element Text=%s", element.getText()));
        return element.getText();
    }

    @Override
    public Boolean isEnabled(){
        System.out.println(String.format("Element Enabled=%b", element.isEnabled()));
        return element.isEnabled();
    }

    @Override
    public Boolean isDisplayed(){
        System.out.println(String.format("Element Displayed=%b", element.isEnabled()));
        return element.isDisplayed();
    }    @Override
    public void typeText(String text){
        System.out.println(String.format("Type Text==%s", text));
        element.typeText(text);
    }
    @Override
    public String getAttribute(String attributeName){
        System.out.println(String.format("Element attribute %s=%s", attributeName, getAttribute(attributeName)));
        return element.getAttribute(attributeName);
    }
}
