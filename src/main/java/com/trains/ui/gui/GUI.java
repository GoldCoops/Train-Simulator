package com.trains.ui.gui;

import com.trains.utils.lang.I18N;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public abstract class GUI extends JPanel {
	private static final float DEFAULT_TITLE_SIZE = 24.0f;
	protected static final int MAX_BUTTON_SIZE = 300;
	protected final JFrame frame;
	protected final JPanel buttonContainer;
	protected float titleSize = DEFAULT_TITLE_SIZE;

	protected GUI previous;

	private final JLabel menuTitle;
	private final List<Runnable> i18nRefreshers = new ArrayList<>();
	private final Runnable localeRefresh = this::refreshI18n;

	public GUI(JFrame frame, GUI previous) {
		super(new BorderLayout());
		this.frame = frame;
		this.previous = previous;
		this.buttonContainer = new JPanel();
		buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
		buttonContainer.setOpaque(false);
		buttonContainer.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

		menuTitle = new JLabel("", SwingConstants.CENTER);
		applyTitleFont();
		bind(menuTitle, getTitle());

		JPanel titleContainer = new JPanel(new BorderLayout());
titleContainer.setOpaque(false);
titleContainer.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
titleContainer.add(menuTitle, BorderLayout.CENTER);

buttonContainer.add(Box.createVerticalGlue());
drawMenuItems();
buttonContainer.add(Box.createVerticalGlue());

		add(titleContainer, BorderLayout.NORTH);
		add(buttonContainer, BorderLayout.CENTER);

		if (previous != null) {
			JPanel backButtonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
			backButtonContainer.setOpaque(false);
			backButtonContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
			backButtonContainer.add(createBackButton());
			add(backButtonContainer, BorderLayout.SOUTH);
		}

		I18N.addListener(localeRefresh);
	}

	protected abstract String getTitle();

	protected abstract void drawMenuItems();

	protected void setTitleSize(float size) {
		this.titleSize = size;
		applyTitleFont();
	}

	protected JButton createMenuButton(String label, Runnable action) {
		JButton button = new JButton();
		bind(button, label);
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		lockButtonSize(button, MAX_BUTTON_SIZE);
		button.addActionListener(event -> action.run());
		return button;
	}

	protected JPanel createButtonRow(JButton... buttons) {
		int spacing = 15;
		JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, spacing, 0));
		buttonRow.setOpaque(false);
		buttonRow.setAlignmentX(Component.CENTER_ALIGNMENT);

		int gaps = buttons.length + 1;
		int buttonWidth = (MAX_BUTTON_SIZE - spacing * gaps) / buttons.length;
		for (JButton button : buttons) {
			lockButtonSize(button, buttonWidth);
			buttonRow.add(button);
		}

		buttonRow.setMaximumSize(new Dimension(MAX_BUTTON_SIZE, buttonRow.getPreferredSize().height));
		return buttonRow;
	}

	protected static void lockButtonSize(AbstractButton button, int width) {
		int height = button.getPreferredSize().height;
		Dimension size = new Dimension(width, height);
		button.setPreferredSize(size);
		button.setMinimumSize(size);
		button.setMaximumSize(size);
	}

	protected JButton createBackButton() {
		return createMenuButton("menu.button.back", () -> openMenu(previous));
	}

	protected void addMenuItems(JComponent... items) {
		for (JComponent item : items) {
			if (buttonContainer.getComponentCount() > 0) {
				buttonContainer.add(Box.createRigidArea(new Dimension(0, 15)));
			}
			item.setAlignmentX(Component.CENTER_ALIGNMENT);
			buttonContainer.add(item);
		}
	}

	protected void bind(JLabel label, String key) {
		Runnable refresher = () -> label.setText(I18N.getString(key));
		i18nRefreshers.add(refresher);
		refresher.run();
	}

	protected void bind(AbstractButton button, String key) {
		Runnable refresher = () -> button.setText(I18N.getString(key));
		i18nRefreshers.add(refresher);
		refresher.run();
	}

	protected void bind(Runnable refresher) {
		i18nRefreshers.add(refresher);
		refresher.run();
	}

	protected void openMenu(GUI menu) {
		I18N.removeListener(localeRefresh);
		menu.attachLocale();
		frame.setContentPane(menu);
		frame.revalidate();
		frame.repaint();
	}

	private void attachLocale() {
		I18N.removeListener(localeRefresh);
		I18N.addListener(localeRefresh);
		refreshI18n();
	}

	private void refreshI18n() {
		for (Runnable refresher : i18nRefreshers) {
			refresher.run();
		}
	}

	private void applyTitleFont() {
		menuTitle.setFont(menuTitle.getFont().deriveFont(Font.BOLD, titleSize));
	}
}
