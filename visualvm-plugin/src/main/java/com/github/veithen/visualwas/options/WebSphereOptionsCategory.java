/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2020 Andreas Veithen
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
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
