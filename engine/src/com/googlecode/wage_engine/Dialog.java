package com.googlecode.wage_engine;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class Dialog extends WComponent {
	private String text;
	private DialogButton[] buttons;
	private DialogButton defaultButton;
	private DialogButton pressedButton;
	private boolean mouseOverPressedButton;
	private ActionListener actionListener;
	
	protected Dialog(ActionListener actionListener, String text, DialogButton[] buttons, int defaultButtonIndex) {
		setSize(292, 114);
		this.actionListener = actionListener;
		this.text = text;
		this.buttons = buttons;
		defaultButton = buttons[defaultButtonIndex];
	}

	private boolean checkBounds(DialogButton button, int x, int y) {
		if (button == null)
			return false;
		Rectangle bounds = new Rectangle(
			button.bounds.x + 5, button.bounds.y + 5,
			button.bounds.width - 10, button.bounds.height - 10);
		return bounds.contains(new Point(x, y));
	}
	
	@Override
	public void handleMouseEvent(int type, int x, int y) {
		if (type == MOUSE_PRESSED) {
			for (DialogButton button : Dialog.this.buttons) {
				if (checkBounds(button, x, y)) {
					pressedButton = button;
					mouseOverPressedButton = true;
					repaint();
					break;
				}
			}
		} else if (type == MOUSE_RELEASED) {
			boolean pressed = checkBounds(pressedButton, x, y);
			ActionEvent clickEvent = null;
			if (pressed && actionListener != null) {
				clickEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, pressedButton.text);
			}
			pressedButton = null;
			mouseOverPressedButton = false;
			repaint();
			if (clickEvent != null) {
				actionListener.actionPerformed(clickEvent);
			}
		} else if (type == MOUSE_MOVED || type == MOUSE_DRAGGED) {
			boolean over = checkBounds(pressedButton, x, y);
			if (over != mouseOverPressedButton) {
				mouseOverPressedButton = over;
				repaint();
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		Font font = getFont();
		if (font == null)
			return;
		FontMetrics metrics = g.getFontMetrics(font);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.WHITE);
		Rectangle bounds = new Rectangle(0, 0, getWidth(), getHeight());
		g2d.fill(bounds);
		g2d.setColor(Color.BLACK);
		g2d.drawString(text, 24, 32);
		drawOutline(g2d, bounds, new int[] { 1, 0, 0, 1, 1 });
		for (DialogButton button : buttons) {
			int[] outline = new int[] { 0, 0, 0, 0, 1 };
			if (button == defaultButton) {
				outline[0] = outline[1] = 1;
			}
			if (pressedButton == button && mouseOverPressedButton) {
				g2d.setColor(Color.BLACK);
				g2d.fillRect(button.bounds.x + 5, button.bounds.y + 5,
					button.bounds.width - 10, button.bounds.height - 10);
				g2d.setColor(Color.WHITE);
			}
			int w = metrics.stringWidth(button.text);
			int x = button.bounds.x + (button.bounds.width - w) / 2;
			int y = button.bounds.y + 19;
			g2d.drawString(button.text, x, y);
			g2d.setColor(Color.BLACK);
			drawOutline(g2d, button.bounds, outline);
		}
	}

	private void drawOutline(Graphics2D g2d, Rectangle bounds, int[] spec) {
		for (int i = 0; i < spec.length; i++) {
			if (spec[i] != 0) {
				g2d.drawRect(bounds.x + i, bounds.y + i, bounds.width - 1 - 2*i, bounds.height - 1 - 2*i);
			}
		}
	}

	protected static class DialogButton {
		String text;
		Rectangle bounds;
		public DialogButton(String text, Rectangle bounds) {
			this.text = text;
			this.bounds = bounds;
		}
	}
}
