package view.components;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

@SuppressWarnings("serial")
/** 
 * A Document object that has a character input limit.
 */
public class JTextFieldLimit extends PlainDocument {
    private int limit;
    public JTextFieldLimit(int limit) {
        super();
        this.limit = limit;
    }

    public JTextFieldLimit(int limit, boolean upper) {
        super();
        this.limit = limit;
    }

    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null)
            return;
        if ((getLength() + str.length()) <= limit)
            super.insertString(offset, str, attr);
    }
}