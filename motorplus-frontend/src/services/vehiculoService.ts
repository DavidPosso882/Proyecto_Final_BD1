import apiClient from './apiClient';

export interface VehiculoDTO {
  placa: string;
  tipo: string;
  marca: string;
  modelo: string;
  anio: number;
  idCliente: number;
  fechaCreacion?: string;
  fechaModificacion?: string;
}

export const vehiculoService = {
  async getAll(): Promise<VehiculoDTO[]> {
    const response = await apiClient.get('/vehiculos');
    return response.data;
  },

  async getByPlaca(placa: string): Promise<VehiculoDTO> {
    const response = await apiClient.get(`/vehiculos/${placa}`);
    return response.data;
  },

  async create(vehiculo: VehiculoDTO): Promise<VehiculoDTO> {
    const response = await apiClient.post('/vehiculos', vehiculo);
    return response.data;
  },

  async update(placa: string, vehiculo: VehiculoDTO): Promise<VehiculoDTO> {
    const response = await apiClient.put(`/vehiculos/${placa}`, vehiculo);
    return response.data;
  },

  async delete(placa: string): Promise<void> {
    await apiClient.delete(`/vehiculos/${placa}`);
  },
};