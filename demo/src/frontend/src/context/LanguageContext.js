import React, { createContext, useState, useContext } from 'react';

const LanguageContext = createContext();

const translations = {
  en: {
    // Navigation
    dashboard: 'Dashboard',
    myFields: 'My Fields',
    planting: 'Planting',
    weather: 'Weather',
    community: 'Community',
    marketplace: 'Marketplace',
    profile: 'Profile',

    // Dashboard
    welcome: 'Welcome',
    activeFields: 'Active Fields',
    activePlantings: 'Active Plantings',
    upcomingTasks: 'Upcoming Tasks',
    weatherToday: 'Weather Today',

    // Fields
    createField: 'Create Field',
    fieldName: 'Field Name',
    area: 'Area (hectares)',
    location: 'Location',
    soilType: 'Soil Type',
    irrigationType: 'Irrigation Type',

    // Weather
    temperature: 'Temperature',
    humidity: 'Humidity',
    rainfall: 'Rainfall',
    forecast: 'Forecast',

    // Actions
    save: 'Save',
    cancel: 'Cancel',
    edit: 'Edit',
    delete: 'Delete',
    create: 'Create',
    view: 'View',
  },

  fil: {
    // Navigation
    dashboard: 'Dashboard',
    myFields: 'Aking mga Bukid',
    planting: 'Pagtatanim',
    weather: 'Panahon',
    community: 'Komunidad',
    marketplace: 'Pamilihan',
    profile: 'Profile',

    // Dashboard
    welcome: 'Maligayang pagdating',
    activeFields: 'Aktibong Bukid',
    activePlantings: 'Aktibong Tanim',
    upcomingTasks: 'Paparating na Gawain',
    weatherToday: 'Panahon Ngayon',

    // Fields
    createField: 'Gumawa ng Bukid',
    fieldName: 'Pangalan ng Bukid',
    area: 'Sukat (ektarya)',
    location: 'Lokasyon',
    soilType: 'Uri ng Lupa',
    irrigationType: 'Uri ng Patubig',

    // Weather
    temperature: 'Temperatura',
    humidity: 'Kahalumigmigan',
    rainfall: 'Ulan',
    forecast: 'Hula sa Panahon',

    // Actions
    save: 'I-save',
    cancel: 'Kanselahin',
    edit: 'I-edit',
    delete: 'Tanggalin',
    create: 'Gumawa',
    view: 'Tingnan',
  },
};

export function LanguageProvider({ children }) {
  const [language, setLanguage] = useState('en');

  const t = (key) => {
    return translations[language][key] || key;
  };

  const toggleLanguage = () => {
    setLanguage(language === 'en' ? 'fil' : 'en');
  };

  return (
    <LanguageContext.Provider value={{ language, setLanguage, t, toggleLanguage }}>
      {children}
    </LanguageContext.Provider>
  );
}

export function useLanguage() {
  const context = useContext(LanguageContext);
  if (!context) {
    throw new Error('useLanguage must be used within LanguageProvider');
  }
  return context;
}