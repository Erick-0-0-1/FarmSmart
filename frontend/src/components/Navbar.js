import React, { useState, useEffect } from 'react';
import { useLanguage } from '../context/LanguageContext';
import { useTheme } from '../context/ThemeContext';

function Navbar({ currentPage, setCurrentPage, onLogout }) {
  const { t, language, toggleLanguage } = useLanguage();
  const { isDark, toggleTheme } = useTheme();
  const [username, setUsername] = useState('');

  useEffect(() => {
    // Get username from localStorage
    const storedUsername = localStorage.getItem('username');
    if (storedUsername) {
      setUsername(storedUsername);
    }
  }, []);

  const navItems = [
    { id: 'dashboard', label: t('dashboard'), icon: '📊' },
    { id: 'fields', label: t('myFields'), icon: '🌾' },
    { id: 'planting', label: t('planting'), icon: '🌱' },
    { id: 'fertilizer', label: 'Fertilizer', icon: '💚' },
    { id: 'weather', label: t('weather'), icon: '🌤️' },
    { id: 'marketplace', label: t('marketplace'), icon: '🛒' },
    { id: 'community', label: t('community'), icon: '👥' },
  ];

  return (
    <nav className="bg-white dark:bg-gray-800 shadow-md transition-colors duration-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo / Username */}
          <div className="flex items-center">
            <h1 className="text-3xl font-bold text-green-600 dark:text-green-400">
              {username ? `🌾  ${username}` : '🌾 EJ Farm'}
            </h1>
          </div>

          {/* Desktop Navigation */}
          <div className="hidden md:flex space-x-4">
            {navItems.map((item) => (
              <button
                key={item.id}
                onClick={() => setCurrentPage(item.id)}
                className={`
                  px-3 py-2 rounded-md text-sm font-medium transition
                  ${currentPage === item.id
                    ? 'bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300'
                    : 'text-gray-600 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700'
                  }
                `}
              >
                <span className="mr-1">{item.icon}</span>
                {item.label}
              </button>
            ))}
          </div>

          {/* Right Side Buttons */}
          <div className="flex items-center gap-2">
            {/* Logout Button (text) */}
            <button
              onClick={onLogout}
              className="px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition text-sm font-medium"
            >
              Logout
            </button>

            {/* Dark Mode Toggle */}
            <button
              onClick={toggleTheme}
              className="p-2 rounded-lg bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 transition"
              title={isDark ? 'Light Mode' : 'Dark Mode'}
            >
              {isDark ? '☀️' : '🌙'}
            </button>

            {/* Language Toggle */}
            <button
              onClick={toggleLanguage}
              className="px-4 py-2 bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300 rounded-lg hover:bg-green-200 dark:hover:bg-green-800 transition"
            >
              {language === 'en' ? '🇵🇭 Filipino' : '🇺🇸 English'}
            </button>
          </div>
        </div>

        {/* Mobile Navigation (unchanged) */}
        <div className="md:hidden pb-3 flex overflow-x-auto space-x-2">
          {navItems.map((item) => (
            <button
              key={item.id}
              onClick={() => setCurrentPage(item.id)}
              className={`
                px-3 py-2 rounded-md text-xs font-medium whitespace-nowrap transition
                ${currentPage === item.id
                  ? 'bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300'
                  : 'text-gray-600 bg-gray-50 dark:text-gray-300 dark:bg-gray-700'
                }
              `}
            >
              {item.icon} {item.label}
            </button>
          ))}
        </div>
      </div>
    </nav>
  );
}

export default Navbar;