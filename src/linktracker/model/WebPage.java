package linktracker.model;


import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Usuario
 */
public class WebPage {
    private String web_page;
    private String url_page;
    private List<String> list_pages;

    public WebPage(String web_page, String url_page) {
        this.web_page = web_page;
        this.url_page = url_page;
    }

    public String getWeb_page() {
        return web_page;
    }

    public void setWeb_page(String web_page) {
        this.web_page = web_page;
    }

    public String getUrl_page() {
        return url_page;
    }

    public void setUrl_page(String url_page) {
        this.url_page = url_page;
    }

    public List<String> getList_pages() {
        return list_pages;
    }

    public void setList_pages(List<String> list_pages) {
        this.list_pages = list_pages;
    }
    
    
}
