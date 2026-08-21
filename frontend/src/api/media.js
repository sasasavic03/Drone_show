import { request, ENDPOINTS } from './client.js'

function normalize(item) {
  return {
    id: item.id,
    title: item.title ?? '',
    type: String(item.type ?? 'PHOTO').toUpperCase(),
    eventType: item.eventType ?? null,
    url: item.fileUrl ?? item.url,
    thumbnail: item.thumbnailUrl ?? item.fileUrl ?? item.url,
  }
}

export async function getMedia({ eventType, type } = {}) {
  const params = new URLSearchParams()
  if (eventType) params.set('eventType', eventType)
  if (type) params.set('type', type)

  const qs = params.toString()
  const data = await request(`${ENDPOINTS.media}${qs ? `?${qs}` : ''}`, { auth: false })

  const list = Array.isArray(data) ? data : (data?.content ?? [])
  return list.map(normalize)
}
