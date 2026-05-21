package co.edu.uniquindio.read_now.service;

import co.edu.uniquindio.read_now.dto.request.PqrActualizarEstadoRequestDTO;
import co.edu.uniquindio.read_now.dto.request.PqrCrearRequestDTO;
import co.edu.uniquindio.read_now.dto.request.PqrMensajeRequestDTO;
import co.edu.uniquindio.read_now.dto.response.PqrAdminResumenResponseDTO;
import co.edu.uniquindio.read_now.dto.response.PqrDetalleResponseDTO;
import co.edu.uniquindio.read_now.dto.response.PqrResumenResponseDTO;
import co.edu.uniquindio.read_now.model.enums.EstadoPqr;
import co.edu.uniquindio.read_now.model.enums.TipoPqr;

import java.util.List;

public interface IPqrService {

    PqrDetalleResponseDTO crearPqr(Long usuarioId, PqrCrearRequestDTO request);

    List<PqrResumenResponseDTO> listarMisPqrs(Long usuarioId);

    PqrDetalleResponseDTO obtenerDetalleLector(Long usuarioId, Long pqrId);

    PqrDetalleResponseDTO agregarMensajeLector(Long usuarioId, Long pqrId, PqrMensajeRequestDTO request);

    List<PqrAdminResumenResponseDTO> listarPqrsAdmin(EstadoPqr estado, TipoPqr tipo);

    PqrDetalleResponseDTO obtenerDetalleAdmin(Long pqrId);

    PqrDetalleResponseDTO actualizarEstadoAdmin(Long adminId, Long pqrId, PqrActualizarEstadoRequestDTO request);

    PqrDetalleResponseDTO agregarMensajeAdmin(Long adminId, Long pqrId, PqrMensajeRequestDTO request);
}
