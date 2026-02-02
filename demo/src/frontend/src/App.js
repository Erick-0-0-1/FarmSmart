import React from 'react';
import { LanguageProvider } from './context/LanguageContext';
import { ThemeProvider } from './context/ThemeContext';
import Dashboard from './pages/Dashboard';

function App() {
  return (
    <ThemeProvider>
      <LanguageProvider>
        <Dashboard />
      </LanguageProvider>
    </ThemeProvider>
  );
}

export default App;