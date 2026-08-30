/**
 * Bug Condition Exploration Test
 *
 * Validates: Requirements 1.1, 1.2, 1.3, 1.4, 2.1, 2.2, 2.3
 *
 * This test verifies that the 5 inventory quota pages render
 * InventoryQuotaTable (table UI) instead of InventoryQuotaCalendar (calendar UI).
 *
 * EXPECTED: This test FAILS on unfixed code because all 5 pages
 * currently import and render InventoryQuotaCalendar.
 */
import fs from 'fs'
import path from 'path'

// ---- Mock all external dependencies ----

// Mock InventoryQuotaCalendar
jest.mock('../InventoryQuotaCalendar', () => {
  const React = require('react')
  return function MockCalendar() {
    return React.createElement('div', { 'data-testid': 'inventory-quota-calendar' }, 'InventoryQuotaCalendar')
  }
})

// Mock InventoryQuotaTable (virtual: true because the file doesn't exist yet in unfixed code)
jest.mock('../InventoryQuotaTable', () => {
  const React = require('react')
  return function MockTable() {
    return React.createElement('div', { 'data-testid': 'inventory-quota-table' }, 'InventoryQuotaTable')
  }
}, { virtual: true })

// Mock api
jest.mock('../../../utils/api', () => {
  const mockApi = {
    get: jest.fn().mockResolvedValue({ data: [] }),
    post: jest.fn().mockResolvedValue({ data: {} }),
  }
  return {
    __esModule: true,
    default: mockApi,
    ratePlanApi: {
      getRatePlans: jest.fn().mockResolvedValue({ data: [] }),
      getRatePlansByHotelCode: jest.fn().mockResolvedValue({ data: [] }),
    },
    hotelRoomTypeApi: {
      getHotelRoomTypes: jest.fn().mockResolvedValue({ data: [] }),
      getHotelRoomTypesByCode: jest.fn().mockResolvedValue({ data: [] }),
    },
  }
})

// Mock HotelContext
jest.mock('../../../contexts/HotelContext', () => ({
  useHotelContext: () => ({
    selectedHotel: 'TEST001',
    selectedHotelId: 1,
    hotels: [],
    loading: false,
    error: null,
    changeHotel: jest.fn(),
    fetchHotels: jest.fn(),
  }),
}))

// Mock AuthContext
jest.mock('../../../contexts/AuthContext', () => {
  const React = require('react')
  return {
    AuthContext: React.createContext({
      user: { name: 'TestUser', username: 'testuser' },
      token: 'test-token',
      isAuthenticated: true,
    }),
  }
})

// Mock antd components
jest.mock('antd', () => {
  const React = require('react')
  const Select = (props) => React.createElement('select', { 'data-testid': 'mock-select' }, props.children)
  Select.Option = (props) => React.createElement('option', { value: props.value }, props.children)
  const Space = (props) => React.createElement('div', null, props.children)
  const Row = (props) => React.createElement('div', null, props.children)
  const Col = (props) => React.createElement('div', null, props.children)
  return { Select, Space, Row, Col }
})

// ---- Now import testing utilities ----
import { act, render } from '@testing-library/react'
import '@testing-library/jest-dom'

// ---- Import page components ----
import PriceLevelInventory from '../PriceLevelInventory'
import ChannelLevelInventory from '../ChannelLevelInventory'
import MarketLevelInventory from '../MarketLevelInventory'
import ChannelRoomTypeInventory from '../ChannelRoomTypeInventory'
import RateCategoryLevelInventory from '../RateCategoryLevelInventory'

// ---- Source file paths for static analysis ----
const INVENTORY_DIR = path.resolve(__dirname, '..')

const PAGE_FILES = [
  { name: 'PriceLevelInventory', file: 'PriceLevelInventory.jsx' },
  { name: 'ChannelLevelInventory', file: 'ChannelLevelInventory.jsx' },
  { name: 'MarketLevelInventory', file: 'MarketLevelInventory.jsx' },
  { name: 'ChannelRoomTypeInventory', file: 'ChannelRoomTypeInventory.jsx' },
  { name: 'RateCategoryLevelInventory', file: 'RateCategoryLevelInventory.jsx' },
]

describe('Bug Condition: Inventory quota pages should render InventoryQuotaTable, not InventoryQuotaCalendar', () => {

  // Static analysis: check source code imports
  describe('Source code import analysis', () => {
    PAGE_FILES.forEach(({ name, file }) => {
      test(`${name} should import InventoryQuotaTable (not InventoryQuotaCalendar)`, () => {
        const filePath = path.join(INVENTORY_DIR, file)
        const source = fs.readFileSync(filePath, 'utf-8')

        // The page SHOULD import InventoryQuotaTable
        expect(source).toMatch(/import\s+\w+\s+from\s+['"]\.\/InventoryQuotaTable['"]/)

        // The page should NOT import InventoryQuotaCalendar
        expect(source).not.toMatch(/import\s+\w+\s+from\s+['"]\.\/InventoryQuotaCalendar['"]/)
      })
    })
  })

  // Render analysis: check which component gets rendered
  describe('Render analysis', () => {
    const pages = [
      { name: 'PriceLevelInventory', Component: PriceLevelInventory },
      { name: 'ChannelLevelInventory', Component: ChannelLevelInventory },
      { name: 'MarketLevelInventory', Component: MarketLevelInventory },
      { name: 'ChannelRoomTypeInventory', Component: ChannelRoomTypeInventory },
      { name: 'RateCategoryLevelInventory', Component: RateCategoryLevelInventory },
    ]

    pages.forEach(({ name, Component }) => {
      test(`${name} should render InventoryQuotaTable component`, async () => {
        let container
        await act(async () => {
          container = render(<Component />).container
        })

        // Should render the Table mock
        const tableEl = container.querySelector('[data-testid="inventory-quota-table"]')
        expect(tableEl).not.toBeNull()

        // Should NOT render the Calendar mock
        const calendarEl = container.querySelector('[data-testid="inventory-quota-calendar"]')
        expect(calendarEl).toBeNull()
      })
    })
  })
})
