package co.edu.uniquindio.read_now.service;

import co.edu.uniquindio.read_now.dto.request.ChatMensajeRequestDTO;

public interface IChatbotService {

    String responder(Long usuarioId, ChatMensajeRequestDTO request);
}
