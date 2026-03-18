import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { LanguageProvider } from './context/LanguageContext';
import { ThemeProvider } from './context/ThemeContext';
import Dashboard from './pages/Dashboard';
import Login from './pages/Login';
import PlantingPage from './pages/PlantingPage';
import FertilizerSchedulePage from './pages/FertilizerSchedulePage';
import WeatherCalendar from './pages/WeatherCalendar';
// Import other pages as needed (e.g., MyFields, Marketplace, Community)

// Protected route component – redirects to login if not authenticated
const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem('token');
  if (!token) {
    return <Navigate to="/login" replace />;
  }
  return children;
};

function App() {
  return (
    <ThemeProvider>
      <LanguageProvider>
        <Router>
          <Routes>
            {/* Public route */}
            <Route path="/login" element={<Login />} />

            {/* Protected routes */}
            <Route
              path="/dashboard"
              element={
                <ProtectedRoute>
                  <Dashboard />
                </ProtectedRoute>
              }
            />
            <Route
              path="/planting"
              element={
                <ProtectedRoute>
                  <PlantingPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="/fertilizer"
              element={
                <ProtectedRoute>
                  <FertilizerSchedulePage />
                </ProtectedRoute>
              }
            />
            <Route
              path="/weather"
              element={
                <ProtectedRoute>
                  <WeatherCalendar />
                </ProtectedRoute>
              }
            />
            {/* Redirect root to dashboard (or login) */}
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
          </Routes>
        </Router>
      </LanguageProvider>
    </ThemeProvider>
  );
}

export default App;