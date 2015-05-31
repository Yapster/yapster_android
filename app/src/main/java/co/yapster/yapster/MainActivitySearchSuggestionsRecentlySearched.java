package co.yapster.yapster;

import android.content.SearchRecentSuggestionsProvider;

public class MainActivitySearchSuggestionsRecentlySearched extends SearchRecentSuggestionsProvider {
	public final static String AUTHORITY = "co.yapster.yapster.MainActivitySearchSuggestionsRecentlySearched";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public MainActivitySearchSuggestionsRecentlySearched() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
