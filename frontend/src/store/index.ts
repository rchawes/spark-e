import { configureStore } from '@reduxjs/toolkit';
import { persistStore, persistReducer } from 'redux-persist';
import storage from 'redux-persist/lib/storage';
import { combineReducers } from '@reduxjs/toolkit';

// Import slices
import authSlice from './slices/authSlice';
import customerSlice from './slices/customerSlice';
import jobSlice from './slices/jobSlice';
import invoiceSlice from './slices/invoiceSlice';
import dashboardSlice from './slices/dashboardSlice';
import uiSlice from './slices/uiSlice';

// Persist configuration
const persistConfig = {
  key: 'root',
  storage,
  whitelist: ['auth', 'ui'], // Only persist auth and UI state
  blacklist: ['dashboard'], // Don't persist dashboard (fetch fresh)
};

// Root reducer
const rootReducer = combineReducers({
  auth: authSlice,
  customers: customerSlice,
  jobs: jobSlice,
  invoices: invoiceSlice,
  dashboard: dashboardSlice,
  ui: uiSlice,
});

// Persisted reducer
const persistedReducer = persistReducer(persistConfig, rootReducer);

// Configure store
export const store = configureStore({
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: ['persist/PERSIST', 'persist/REHYDRATE'],
      },
    }),
  devTools: process.env.NODE_ENV !== 'production',
});

// Persistor
export const persistor = persistStore(store);

// Export types
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

// Selectors
export const selectAuth = (state: RootState) => state.auth;
export const selectCustomers = (state: RootState) => state.customers;
export const selectJobs = (state: RootState) => state.jobs;
export const selectInvoices = (state: RootState) => state.invoices;
export const selectDashboard = (state: RootState) => state.dashboard;
export const selectUI = (state: RootState) => state.ui;
