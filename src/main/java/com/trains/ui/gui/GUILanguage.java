package com.trains.ui.gui;

import com.trains.utils.lang.I18N;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GUILanguage extends GUI {

	public GUILanguage(JFrame frame, GUI previous) {
		super(frame, previous);
	}

	@Override
	protected String getTitle() {
		return "menu.title.language";
	}

	@Override
	protected void drawMenuItems() {
		JPanel toggleHolder = new JPanel();
		toggleHolder.setLayout(new BoxLayout(toggleHolder, BoxLayout.Y_AXIS));
		toggleHolder.setOpaque(false);

		ButtonGroup toggleGroup = new ButtonGroup();
		List<Locale> locales = new ArrayList<>(I18N.availableLocales);

		for (Locale locale : locales) {
			JToggleButton button = new JToggleButton(locale.getDisplayName());
			toggleGroup.add(button);
			button.setSelected(locale.equals(I18N.getLocale()));
			button.setAlignmentX(Component.CENTER_ALIGNMENT);
			button.setMaximumSize(new Dimension(MAX_BUTTON_SIZE, Integer.MAX_VALUE));
			button.setPreferredSize(new Dimension(MAX_BUTTON_SIZE, button.getPreferredSize().height));
			button.addActionListener(event -> I18N.setLocale(locale));
			toggleHolder.add(button);
		}

		JScrollPane scrollPane = new JScrollPane(toggleHolder);
		scrollPane.setBorder(null);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		addMenuItems(scrollPane);
	}
}
