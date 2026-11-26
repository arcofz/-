import http from './http'

// GET /categories、GET /locations（公开，返回 { list: [...] }）
export async function getCategories() {
  const data = await http.get('/categories')
  return data.list || []
}

export async function getLocations() {
  const data = await http.get('/locations')
  return data.list || []
}
