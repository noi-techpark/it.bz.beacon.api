package it.bz.beacon.api.kontakt.io.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.net.URL;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchMeta {

    private String filter;
    private int startIndex;
    private int maxResult;
    private URL prevResults;
    private int count;
    private String orderBy;
    private URL nextResults;
    private QueryType queryType;
    private Order order;

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getMaxResult() {
        return maxResult;
    }

    public void setMaxResult(int maxResult) {
        this.maxResult = maxResult;
    }

    public URL getPrevResults() {
        return prevResults;
    }

    public void setPrevResults(URL prevResults) {
        this.prevResults = prevResults;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public URL getNextResults() {
        return nextResults;
    }

    public void setNextResults(URL nextResults) {
        this.nextResults = nextResults;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public enum QueryType {
        NORMAL,
        COUNTED,
        SEARCH_META
    }

    public enum Order {
        ASC,
        DESC
    }
}
