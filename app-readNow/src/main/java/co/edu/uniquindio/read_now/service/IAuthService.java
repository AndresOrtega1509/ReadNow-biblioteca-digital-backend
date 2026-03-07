package co.edu.uniquindio.read_now.service;

import co.edu.uniquindio.read_now.dto.request.*;

import co.edu.uniquindio.read_now.dto.response.LoginResponseDTO;
import co.edu.uniquindio.read_now.dto.response.LoginResultDTO;
import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;


public interface IAuthService {

    MensajeResponseDTO registrar(RegistroRequestDTO request);

    LoginResultDTO login(LoginRequestDTO request);

    LoginResponseDTO verificarCodigo(VerificacionCodigoRequestDTO request);

    MensajeResponseDTO recuperarPassword(RecuperarPasswordRequestDTO request);

    MensajeResponseDTO restablecerPassword(RestablecerPasswordRequestDTO request);

    MensajeResponseDTO verificarTelefonoRecuperacion(VerificarTelefonoRecuperacionRequestDTO request);
}
