import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, of } from 'rxjs';

export interface Country {
  name: {
    common: string;
    official: string;
    nativeName?: { [key: string]: { official: string; common: string } };
  };
  flags: {
    png: string;
    svg: string;
    alt: string;
  };
  cca2?: string;
  cca3?: string;
  region?: string;
  subregion?: string;
  capital?: string[];
  languages?: { [key: string]: string };
  currencies?: { [key: string]: any };
}

export interface State {
  name: string;
  abbreviation: string;
}

export interface City {
  name: string;
  state: string;
}

@Injectable({
  providedIn: 'root'
})
export class LocationService {
  private readonly countriesApiUrl = 'https://restcountries.com/v3.1';
  
  // US States data
  private readonly usStates: State[] = [
    { name: 'Alabama', abbreviation: 'AL' },
    { name: 'Alaska', abbreviation: 'AK' },
    { name: 'Arizona', abbreviation: 'AZ' },
    { name: 'Arkansas', abbreviation: 'AR' },
    { name: 'California', abbreviation: 'CA' },
    { name: 'Colorado', abbreviation: 'CO' },
    { name: 'Connecticut', abbreviation: 'CT' },
    { name: 'Delaware', abbreviation: 'DE' },
    { name: 'Florida', abbreviation: 'FL' },
    { name: 'Georgia', abbreviation: 'GA' },
    { name: 'Hawaii', abbreviation: 'HI' },
    { name: 'Idaho', abbreviation: 'ID' },
    { name: 'Illinois', abbreviation: 'IL' },
    { name: 'Indiana', abbreviation: 'IN' },
    { name: 'Iowa', abbreviation: 'IA' },
    { name: 'Kansas', abbreviation: 'KS' },
    { name: 'Kentucky', abbreviation: 'KY' },
    { name: 'Louisiana', abbreviation: 'LA' },
    { name: 'Maine', abbreviation: 'ME' },
    { name: 'Maryland', abbreviation: 'MD' },
    { name: 'Massachusetts', abbreviation: 'MA' },
    { name: 'Michigan', abbreviation: 'MI' },
    { name: 'Minnesota', abbreviation: 'MN' },
    { name: 'Mississippi', abbreviation: 'MS' },
    { name: 'Missouri', abbreviation: 'MO' },
    { name: 'Montana', abbreviation: 'MT' },
    { name: 'Nebraska', abbreviation: 'NE' },
    { name: 'Nevada', abbreviation: 'NV' },
    { name: 'New Hampshire', abbreviation: 'NH' },
    { name: 'New Jersey', abbreviation: 'NJ' },
    { name: 'New Mexico', abbreviation: 'NM' },
    { name: 'New York', abbreviation: 'NY' },
    { name: 'North Carolina', abbreviation: 'NC' },
    { name: 'North Dakota', abbreviation: 'ND' },
    { name: 'Ohio', abbreviation: 'OH' },
    { name: 'Oklahoma', abbreviation: 'OK' },
    { name: 'Oregon', abbreviation: 'OR' },
    { name: 'Pennsylvania', abbreviation: 'PA' },
    { name: 'Rhode Island', abbreviation: 'RI' },
    { name: 'South Carolina', abbreviation: 'SC' },
    { name: 'South Dakota', abbreviation: 'SD' },
    { name: 'Tennessee', abbreviation: 'TN' },
    { name: 'Texas', abbreviation: 'TX' },
    { name: 'Utah', abbreviation: 'UT' },
    { name: 'Vermont', abbreviation: 'VT' },
    { name: 'Virginia', abbreviation: 'VA' },
    { name: 'Washington', abbreviation: 'WA' },
    { name: 'West Virginia', abbreviation: 'WV' },
    { name: 'Wisconsin', abbreviation: 'WI' },
    { name: 'Wyoming', abbreviation: 'WY' }
  ];

  // Major US Cities data
  private readonly usCities: City[] = [
    { name: 'New York', state: 'New York' },
    { name: 'Los Angeles', state: 'California' },
    { name: 'Chicago', state: 'Illinois' },
    { name: 'Houston', state: 'Texas' },
    { name: 'Phoenix', state: 'Arizona' },
    { name: 'Philadelphia', state: 'Pennsylvania' },
    { name: 'San Antonio', state: 'Texas' },
    { name: 'San Diego', state: 'California' },
    { name: 'Dallas', state: 'Texas' },
    { name: 'San Jose', state: 'California' },
    { name: 'Austin', state: 'Texas' },
    { name: 'Jacksonville', state: 'Florida' },
    { name: 'Fort Worth', state: 'Texas' },
    { name: 'Columbus', state: 'Ohio' },
    { name: 'Charlotte', state: 'North Carolina' },
    { name: 'San Francisco', state: 'California' },
    { name: 'Indianapolis', state: 'Indiana' },
    { name: 'Seattle', state: 'Washington' },
    { name: 'Denver', state: 'Colorado' },
    { name: 'Washington', state: 'District of Columbia' },
    { name: 'Boston', state: 'Massachusetts' },
    { name: 'El Paso', state: 'Texas' },
    { name: 'Nashville', state: 'Tennessee' },
    { name: 'Detroit', state: 'Michigan' },
    { name: 'Oklahoma City', state: 'Oklahoma' },
    { name: 'Portland', state: 'Oregon' },
    { name: 'Las Vegas', state: 'Nevada' },
    { name: 'Memphis', state: 'Tennessee' },
    { name: 'Louisville', state: 'Kentucky' },
    { name: 'Baltimore', state: 'Maryland' },
    { name: 'Milwaukee', state: 'Wisconsin' },
    { name: 'Albuquerque', state: 'New Mexico' },
    { name: 'Tucson', state: 'Arizona' },
    { name: 'Fresno', state: 'California' },
    { name: 'Sacramento', state: 'California' },
    { name: 'Mesa', state: 'Arizona' },
    { name: 'Kansas City', state: 'Missouri' },
    { name: 'Atlanta', state: 'Georgia' },
    { name: 'Long Beach', state: 'California' },
    { name: 'Colorado Springs', state: 'Colorado' },
    { name: 'Raleigh', state: 'North Carolina' },
    { name: 'Miami', state: 'Florida' },
    { name: 'Virginia Beach', state: 'Virginia' },
    { name: 'Omaha', state: 'Nebraska' },
    { name: 'Oakland', state: 'California' },
    { name: 'Minneapolis', state: 'Minnesota' },
    { name: 'Tulsa', state: 'Oklahoma' },
    { name: 'Arlington', state: 'Texas' },
    { name: 'Tampa', state: 'Florida' },
    { name: 'New Orleans', state: 'Louisiana' },
    { name: 'Wichita', state: 'Kansas' },
    { name: 'Cleveland', state: 'Ohio' },
    { name: 'Bakersfield', state: 'California' },
    { name: 'Aurora', state: 'Colorado' },
    { name: 'Anaheim', state: 'California' },
    { name: 'Honolulu', state: 'Hawaii' },
    { name: 'Santa Ana', state: 'California' },
    { name: 'Corpus Christi', state: 'Texas' },
    { name: 'Riverside', state: 'California' },
    { name: 'Lexington', state: 'Kentucky' },
    { name: 'Stockton', state: 'California' },
    { name: 'Henderson', state: 'Nevada' },
    { name: 'Saint Paul', state: 'Minnesota' },
    { name: 'St. Louis', state: 'Missouri' },
    { name: 'Cincinnati', state: 'Ohio' },
    { name: 'Pittsburgh', state: 'Pennsylvania' },
    { name: 'Anchorage', state: 'Alaska' },
    { name: 'Greensboro', state: 'North Carolina' },
    { name: 'Plano', state: 'Texas' },
    { name: 'Newark', state: 'New Jersey' },
    { name: 'Lincoln', state: 'Nebraska' },
    { name: 'Orlando', state: 'Florida' },
    { name: 'Irvine', state: 'California' },
    { name: 'Durham', state: 'North Carolina' },
    { name: 'Buffalo', state: 'New York' },
    { name: 'Chula Vista', state: 'California' },
    { name: 'Jersey City', state: 'New Jersey' },
    { name: 'Chandler', state: 'Arizona' },
    { name: 'Madison', state: 'Wisconsin' },
    { name: 'Laredo', state: 'Texas' },
    { name: 'Lubbock', state: 'Texas' },
    { name: 'Scottsdale', state: 'Arizona' },
    { name: 'Reno', state: 'Nevada' },
    { name: 'Glendale', state: 'Arizona' },
    { name: 'Toledo', state: 'Ohio' },
    { name: 'Fort Wayne', state: 'Indiana' },
    { name: 'Chandler', state: 'Arizona' },
    { name: 'St. Petersburg', state: 'Florida' },
    { name: 'Laredo', state: 'Texas' },
    { name: 'Irving', state: 'Texas' },
    { name: 'Chesapeake', state: 'Virginia' },
    { name: 'Gilbert', state: 'Arizona' },
    { name: 'Hialeah', state: 'Florida' },
    { name: 'Garland', state: 'Texas' },
    { name: 'Fremont', state: 'California' },
    { name: 'Baton Rouge', state: 'Louisiana' },
    { name: 'Richmond', state: 'Virginia' },
    { name: 'Boise', state: 'Idaho' },
    { name: 'Spokane', state: 'Washington' }
  ];

  private cachedCountries: Country[] | null = null;
  private countriesLoaded: boolean = false;

  constructor(private http: HttpClient) {}

  // Get all countries
  getCountries(): Observable<Country[]> {
    return this.http.get<Country[]>(`${this.countriesApiUrl}/all?fields=name,flags`).pipe(
      map(countries => countries.sort((a, b) => a.name.common.localeCompare(b.name.common)))
    );
  }

  // Search countries by name
  searchCountries(query: string): Observable<Country[]> {
    if (!query || query.length < 2) {
      return of([]);
    }
    
    return this.http.get<Country[]>(`${this.countriesApiUrl}/name/${query}?fields=name,flags`).pipe(
      map(countries => countries
        .filter(country => country.name.common.toLowerCase().includes(query.toLowerCase()))
        .sort((a, b) => a.name.common.localeCompare(b.name.common))
        .slice(0, 10) // Limit to 10 results
      )
    );
  }

  // Fetch and cache all countries (with flags)
  loadAllCountries(): Observable<Country[]> {
    if (this.countriesLoaded && this.cachedCountries) {
      return of(this.cachedCountries);
    }
    return this.getCountries().pipe(
      map(countries => {
        this.cachedCountries = countries;
        this.countriesLoaded = true;
        return countries;
      })
    );
  }

  // Search countries locally from cached list
  searchCountriesLocal(query: string): Observable<Country[]> {
    if (!this.countriesLoaded || !this.cachedCountries) {
      // If not loaded yet, load and then filter
      return this.loadAllCountries().pipe(
        map(countries => this.filterCountries(query, countries))
      );
    }
    return of(this.filterCountries(query, this.cachedCountries));
  }

  private filterCountries(query: string, countries: Country[]): Country[] {
    if (!query || query.length < 2) {
      return [];
    }
    return countries
      .filter(country => country.name.common.toLowerCase().includes(query.toLowerCase()))
      .sort((a, b) => a.name.common.localeCompare(b.name.common))
      .slice(0, 10);
  }

  // Get US states
  getUSStates(): Observable<State[]> {
    return of(this.usStates);
  }

  // Search US states
  searchUSStates(query: string): Observable<State[]> {
    if (!query || query.length < 1) {
      return of([]);
    }
    
    const filteredStates = this.usStates.filter(state =>
      state.name.toLowerCase().includes(query.toLowerCase()) ||
      state.abbreviation.toLowerCase().includes(query.toLowerCase())
    );
    
    return of(filteredStates.slice(0, 10)); // Limit to 10 results
  }

  // Get US cities
  getUSCities(): Observable<City[]> {
    return of(this.usCities);
  }

  // Search US cities
  searchUSCities(query: string, stateFilter?: string): Observable<City[]> {
    if (!query || query.length < 2) {
      return of([]);
    }
    
    let filteredCities = this.usCities.filter(city =>
      city.name.toLowerCase().includes(query.toLowerCase())
    );
    
    // Filter by state if provided
    if (stateFilter) {
      filteredCities = filteredCities.filter(city =>
        city.state.toLowerCase().includes(stateFilter.toLowerCase())
      );
    }
    
    return of(filteredCities.slice(0, 10)); // Limit to 10 results
  }

  // Get cities for a specific state
  getCitiesByState(stateName: string): Observable<City[]> {
    const citiesInState = this.usCities.filter(city =>
      city.state.toLowerCase() === stateName.toLowerCase()
    );
    return of(citiesInState);
  }
} 