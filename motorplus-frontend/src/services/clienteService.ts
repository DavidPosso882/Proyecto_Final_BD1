import apiClient from './apiClient';

export interface ClienteDTO {
  idCliente: number;
  nombre: string;
  telefono: string;
  tipo: string;
  fechaCreacion?: string;
  fechaModificacion?: string;
}

export const clienteService = {
  async getAll(): Promise<ClienteDTO[]> {
    const response = await apiClient.get('/clientes');
    return response.data;
  },

  async getById(id: number): Promise<ClienteDTO> {
    const response = await apiClient.get(`/clientes/${id}`);
    return response.data;
  },

  async create(cliente: Omit<ClienteDTO, 'idCliente'>): Promise<ClienteDTO> {
    const response = await apiClient.post('/clientes', cliente);
    return response.data;
  },

  async update(id: number, cliente: ClienteDTO): Promise<ClienteDTO> {
    const response = await apiClient.put(`/clientes/${id}`, cliente);
    return response.data;
  },

  async delete(id: number): Promise<void> {
    await apiClient.delete(`/clientes/${id}`);
  },
};