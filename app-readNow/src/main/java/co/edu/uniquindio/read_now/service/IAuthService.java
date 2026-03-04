package co.edu.uniquindio.read_now.service;

import co.edu.uniquindio.read_now.dto.request.RegistroRequestDTO;

import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;


public interface IAuthService {

    MensajeResponseDTO registrar(RegistroRequestDTO request);

}
