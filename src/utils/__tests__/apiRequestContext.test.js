import { applyRequestContext } from '../api'

describe('API request context', () => {
  beforeEach(() => {
    localStorage.clear()
    sessionStorage.clear()
  })

  test('attaches token, selected tenant and trace id', () => {
    localStorage.setItem('crs_token', 'test-token')
    localStorage.setItem('crs_selected_tenant', '7')

    const config = applyRequestContext({ headers: {} })

    expect(config.headers.Authorization).toBe('Bearer test-token')
    expect(config.headers['X-Tenant-Id']).toBe(7)
    expect(config.headers['X-Trace-Id']).toHaveLength(32)
    expect(sessionStorage.getItem('crs_last_trace_id')).toBe(config.headers['X-Trace-Id'])
  })

  test('falls back to authenticated user tenant', () => {
    localStorage.setItem('crs_user', JSON.stringify({ tenantId: 3 }))

    const config = applyRequestContext({ headers: {} })

    expect(config.headers.Authorization).toBeUndefined()
    expect(config.headers['X-Tenant-Id']).toBe(3)
  })
})
