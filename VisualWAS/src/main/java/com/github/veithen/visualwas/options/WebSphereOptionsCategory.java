package com.github.veithen.visualwas.options;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.netbeans.spi.options.OptionsCategory;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;

public class WebSphereOptionsCategory extends OptionsCategory {
    @Override
    public Icon getIcon() {
        return new ImageIcon(WebSphereOptionsCategory.class.getResource("websphere.png"));
    }

    @Override
    public String getCategoryName() {
        return NbBundle.getMessage(WebSphereOptionsCategory.class, "OptionsCategory_Name_WebSphere");
    }

    @Override
    public String getTitle() {
        return NbBundle.getMessage(WebSphereOptionsCategory.class, "OptionsCategory_Title_WebSphere");
    }


    @Override
    public OptionsPanelController create() {
        return new WebSphereOptionsPanelController();
    }
}
