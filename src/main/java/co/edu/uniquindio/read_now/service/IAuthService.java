package co.edu.uniquindio.read_now.service;

import co.edu.uniquindio.read_now.dto.request.*;

import co.edu.uniquindio.read_now.dto.response.LoginResponseDTO;
import co.edu.uniquindio.read_now.dto.response.LoginResultDTO;
import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;
import co.edu.uniquindio.read_now.dto.response.RecuperarPasswordResponseDTO;


public interface IAuthService {

    MensajeResponseDTO registrar(RegistroRequestDTO request);

    LoginResultDTO login(LoginRequestDTO request);

    /** Reactiva cuenta inactiva con el mismo correo y contraseña; luego mismo flujo que login (2FA o token). */
    LoginResultDTO reactivarCuenta(LoginRequestDTO request);

    LoginResponseDTO verificarCodigo(VerificacionCodigoRequestDTO request);

    RecuperarPasswordResponseDTO recuperarPassword(RecuperarPasswordRequestDTO request);

    /** Verifica últimos 4 dígitos del teléfono; si coinciden, envía por correo un enlace para restablecer contraseña. */
    MensajeResponseDTO verificarTelefonoRecuperacion(VerificarTelefonoRecuperacionRequestDTO request);

    MensajeResponseDTO restablecerPassword(RestablecerPasswordRequestDTO request);
}
