
package com.jovetech.product;

public interface ITabItem {

    public String message = null;
    
    public boolean isCustomize();
    
    public String getName();

    public int getDrawable();

    public void setMessage(String count);
    
    public String getMessage();

    public char getTag();

    /**
     * @return item's type of layout default is 0
     */
    public int getLayoutType();

}
