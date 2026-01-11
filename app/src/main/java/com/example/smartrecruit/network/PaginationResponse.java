package com.example.smartrecruit.network;

import java.util.List;

public class PaginationResponse<T> {
    public int current_page;
    public List<T> data;
    public int last_page;
}
