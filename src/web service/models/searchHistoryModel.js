// This module handles the search history functionality for a web service.
const searchHistory = []

const saveSearchQuery = (userId, query) => {
    // Function to save a search query to the json 
    const searchEntry = {
      userId: userId,
      query: query,
      timestamp: new Date(),
    };
    searchHistory.push(searchEntry);
}

// Function to retrieve the last search queries for a user
const getLastSearchQueries = (userId, limit = 6) => {
    const seen = new Set();

    return searchHistory
        .filter(entry => entry.userId === userId)
        .sort((a, b) => b.timestamp - a.timestamp)
        .filter(entry => {
            if (seen.has(entry.query)) return false;
            seen.add(entry.query);
            return true;
        })
        .slice(0, limit)
        .map(entry => entry.query); 
}

// Function to delete all search queries according to userId
const deleteSearchQueriesByUserId = (userId) => {
    for (let i = searchHistory.length - 1; i >= 0; i--) {
        if (searchHistory[i].userId === userId) {
            searchHistory.splice(i, 1);
        }
    }
}

// Function to delete a specific search query for a user
const deleteOneSearchQuery = (userId, query) => {
    for (let i = searchHistory.length - 1; i >= 0; i--) {
        if (searchHistory[i].userId === userId && searchHistory[i].query === query) {
            searchHistory.splice(i, 1);
        }
    }
};

module.exports = {
    saveSearchQuery,
    getLastSearchQueries,
    deleteSearchQueriesByUserId,
    deleteOneSearchQuery
};