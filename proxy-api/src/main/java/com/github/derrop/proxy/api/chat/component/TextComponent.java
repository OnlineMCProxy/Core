package com.github.derrop.proxy.api.chat.component;

import com.github.derrop.proxy.api.chat.ClickEvent;
import com.github.derrop.proxy.api.util.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextComponent extends BaseComponent {

    private static final Pattern url = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");

    public TextComponent(String text) {
        this.text = text;
    }

    /**
     * Converts the old formatting system that used
     * {@link ChatColor#COLOR_CHAR} into the new json based
     * system.
     *
     * @param message the text to convert
     * @return the components needed to print the message to the client
     */
    public static BaseComponent[] fromLegacyText(String message) {
        return fromLegacyText(message, ChatColor.WHITE);
    }

    /**
     * Converts the old formatting system that used
     * {@link ChatColor#COLOR_CHAR} into the new json based
     * system.
     *
     * @param message      the text to convert
     * @param defaultColor color to use when no formatting is to be applied
     *                     (i.e. after ChatColor.RESET).
     * @return the components needed to print the message to the client
     */
    public static BaseComponent[] fromLegacyText(String message, ChatColor defaultColor) {
        ArrayList<BaseComponent> components = new ArrayList<BaseComponent>();
        StringBuilder builder = new StringBuilder();
        TextComponent component = new TextComponent();
        Matcher matcher = url.matcher(message);

        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (c == ChatColor.COLOR_CHAR) {
                if (++i >= message.length()) {
                    break;
                }
                c = message.charAt(i);
                if (c >= 'A' && c <= 'Z') {
                    c += 32;
                }
                ChatColor format = ChatColor.getByChar(c);
                if (format == null) {
                    continue;
                }
                if (builder.length() > 0) {
                    TextComponent old = component;
                    component = new TextComponent(old);
                    old.setText(builder.toString());
                    builder = new StringBuilder();
                    components.add(old);
                }
                switch (format) {
                    case BOLD:
                        component.setBold(true);
                        break;
                    case ITALIC:
                        component.setItalic(true);
                        break;
                    case UNDERLINE:
                        component.setUnderlined(true);
                        break;
                    case STRIKETHROUGH:
                        component.setStrikethrough(true);
                        break;
                    case MAGIC:
                        component.setObfuscated(true);
                        break;
                    case RESET:
                        format = defaultColor;
                    default:
                        component = new TextComponent();
                        component.setColor(format);
                        break;
                }
                continue;
            }
            int pos = message.indexOf(' ', i);
            if (pos == -1) {
                pos = message.length();
            }
            if (matcher.region(i, pos).find()) { //Web link handling

                if (builder.length() > 0) {
                    TextComponent old = component;
                    component = new TextComponent(old);
                    old.setText(builder.toString());
                    builder = new StringBuilder();
                    components.add(old);
                }

                TextComponent old = component;
                component = new TextComponent(old);
                String urlString = message.substring(i, pos);
                component.setText(urlString);
                component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                        urlString.startsWith("http") ? urlString : "http://" + urlString));
                components.add(component);
                i += pos - i - 1;
                component = old;
                continue;
            }
            builder.append(c);
        }

        component.setText(builder.toString());
        components.add(component);

        return components.toArray(new BaseComponent[components.size()]);
    }

    /**
     * The text of the component that will be displayed to the client
     */
    private String text;

    /**
     * Creates a TextComponent with blank text.
     */
    public TextComponent() {
        this.text = "";
    }

    /**
     * Creates a TextComponent with formatting and text from the passed
     * component
     *
     * @param textComponent the component to copy from
     */
    public TextComponent(TextComponent textComponent) {
        super(textComponent);
        setText(textComponent.getText());
    }

    /**
     * Creates a TextComponent with blank text and the extras set to the passed
     * array
     *
     * @param extras the extras to set
     */
    public TextComponent(BaseComponent... extras) {
        this();
        if (extras.length == 0) {
            return;
        }
        setExtra(new ArrayList<BaseComponent>(Arrays.asList(extras)));
    }

    /**
     * Creates a duplicate of this TextComponent.
     *
     * @return the duplicate of this TextComponent.
     */
    @Override
    public BaseComponent duplicate() {
        return new TextComponent(this);
    }

    @Override
    protected void toPlainText(StringBuilder builder) {
        builder.append(text);
        super.toPlainText(builder);
    }

    @Override
    protected void toLegacyText(StringBuilder builder) {
        addFormat(builder);
        builder.append(text);
        super.toLegacyText(builder);
    }

    @Override
    public String toString() {
        return String.format("TextComponent{text=%s, %s}", text, super.toString());
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof TextComponent)) return false;
        final TextComponent other = (TextComponent) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$text = this.getText();
        final Object other$text = other.getText();
        if (this$text == null ? other$text != null : !this$text.equals(other$text)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof TextComponent;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $text = this.getText();
        result = result * PRIME + ($text == null ? 43 : $text.hashCode());
        return result;
    }
}
