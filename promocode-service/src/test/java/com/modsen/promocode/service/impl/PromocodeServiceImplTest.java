package com.modsen.promocode.service.impl;

import com.modsen.promocode.constants.MessageTemplates;
import com.modsen.promocode.constants.TestConstants;
import com.modsen.promocode.dto.request.PromocodeRequest;
import com.modsen.promocode.dto.request.UpdateDiscountPercentRequest;
import com.modsen.promocode.dto.response.PromocodeListResponse;
import com.modsen.promocode.dto.response.PromocodeResponse;
import com.modsen.promocode.exception.PromocodeAlreadyExistsException;
import com.modsen.promocode.exception.PromocodeNotFoundException;
import com.modsen.promocode.mapper.PromocodeMapperImpl;
import com.modsen.promocode.model.Promocode;
import com.modsen.promocode.repository.PromocodeRepository;
import com.modsen.promocode.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromocodeServiceImplTest {
    @Mock
    private PromocodeRepository promocodeRepository;
    @Mock
    private PromocodeMapperImpl promocodeMapper;
    @InjectMocks
    private PromocodeServiceImpl promocodeService;

    @Test
    void findAllPromocodes_Success() {
        List<Promocode> promocodeList = Collections.nCopies(3, TestUtils.defaultPromocode());

        when(promocodeRepository.findAll())
                .thenReturn(promocodeList);
        when(promocodeMapper.toPromocodeListResponse(anyList()))
                .thenCallRealMethod();

        PromocodeListResponse actualPromocodeList = promocodeService.findAllPromocodes();

        assertEquals(promocodeList.size(), actualPromocodeList.getQuantity());
        verify(promocodeRepository).findAll();
        verify(promocodeMapper).toPromocodeListResponse(promocodeList);
    }

    @Test
    void findPromocodeById_PromocodeExists_ReturnPromocode() {
        Long promocodeId = TestConstants.PROMOCODE_ID;
        Promocode promocode = TestUtils.defaultPromocode();

        when(promocodeRepository.findById(anyLong()))
                .thenReturn(Optional.of(promocode));
        when(promocodeMapper.toPromocodeResponse(any(Promocode.class)))
                .thenCallRealMethod();

        PromocodeResponse actualPromocode = promocodeService.findPromocodeById(promocodeId);

        assertNotNull(actualPromocode);
        verify(promocodeRepository).findById(promocodeId);
        verify(promocodeMapper).toPromocodeResponse(promocode);
    }

    @Test
    void findPromocodeById_PromocodeDoesNotExist_ThrowPromocodeNotFoundException() {
        Long promocodeId = TestConstants.PROMOCODE_ID;
        String exceptionMessage = String.format(MessageTemplates.PROMOCODE_NOT_FOUND_BY_ID.getValue(), promocodeId);

        when(promocodeRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> promocodeService.findPromocodeById(promocodeId))
                .isInstanceOf(PromocodeNotFoundException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void findByName_PromocodeExists_ShouldReturnPromocode() {
        String promocodeName = TestConstants.PROMOCODE_NAME;
        Promocode promocode = TestUtils.defaultPromocode();

        when(promocodeRepository.findByName(anyString()))
                .thenReturn(Optional.of(promocode));

        Promocode actualPromocode = promocodeService.findByName(promocodeName);
        assertEquals(promocodeName, actualPromocode.getName());
        verify(promocodeRepository).findByName(promocodeName);
    }

    @Test
    void findByName_PromocodeDoesNotExist_ThrowPromocodeNotFoundException() {
        String promocodeName = TestConstants.PROMOCODE_NAME;
        String exceptionMessage = String.format(MessageTemplates.PROMOCODE_NOT_FOUND_BY_NAME.getValue(), promocodeName);

        when(promocodeRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> promocodeService.findByName(promocodeName))
                .isInstanceOf(PromocodeNotFoundException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void createPromocode_ValidPromocodeRequest_ShouldReturnCreatedPromocode() {
        Promocode promocode = TestUtils.defaultPromocode();
        PromocodeRequest promocodeRequest = TestUtils.defaultPromocodeRequest();

        when(promocodeMapper.toPromocode(any(PromocodeRequest.class)))
                .thenReturn(promocode);
        when(promocodeRepository.save(any(Promocode.class)))
                .thenReturn(promocode);
        when(promocodeMapper.toPromocodeResponse(any(Promocode.class)))
                .thenCallRealMethod();

        PromocodeResponse createdPromocode = promocodeService.createPromocode(promocodeRequest);

        assertNotNull(createdPromocode);
        verify(promocodeMapper).toPromocode(promocodeRequest);
        verify(promocodeRepository).save(promocode);
        verify(promocodeMapper).toPromocodeResponse(promocode);
    }

    @Test
    void createPromocode_DuplicatePromocodeName_ThrowPromocodeAlreadyExistsException() {
        PromocodeRequest promocodeRequest = TestUtils.defaultPromocodeRequest();
        String exceptionMessage = String.format(MessageTemplates.PROMOCODE_ALREADY_EXISTS.getValue(), promocodeRequest.getName());

        when(promocodeRepository.existsByName(anyString()))
                .thenReturn(true);

        assertThatThrownBy(() -> promocodeService.createPromocode(promocodeRequest))
                .isInstanceOf(PromocodeAlreadyExistsException.class)
                .hasMessage(exceptionMessage);
        verify(promocodeRepository).existsByName(promocodeRequest.getName());
        verify(promocodeRepository, never()).save(any(Promocode.class));
    }

    @Test
    void updatePromocode_ValidUpdateDiscountPercentRequest_ShouldReturnUpdatedPromocode() {
        Promocode promocode = TestUtils.defaultPromocode();
        UpdateDiscountPercentRequest updateDiscountPercentRequest = TestUtils.defaultUpdateDiscountPercentRequest();
        int expectedNewDiscountPercent = updateDiscountPercentRequest.getDiscountPercent();

        when(promocodeRepository.findById(anyLong()))
                .thenReturn(Optional.of(promocode));
        when(promocodeRepository.save(any(Promocode.class)))
                .thenReturn(promocode);
        when(promocodeMapper.toPromocodeResponse(any(Promocode.class)))
                .thenCallRealMethod();

        PromocodeResponse updatedPromocode = promocodeService.updatePromocode(updateDiscountPercentRequest);

        assertNotNull(updatedPromocode);
        assertEquals(expectedNewDiscountPercent, updatedPromocode.discountPercent());
        verify(promocodeRepository).findById(updateDiscountPercentRequest.getPromocodeId());
        verify(promocodeRepository).save(promocode);
        verify(promocodeMapper).toPromocodeResponse(promocode);
    }

    @Test
    void updatePromocode_PromocodeDoesNotExist_ThrowPromocodeNotFoundException() {
        UpdateDiscountPercentRequest updateDiscountPercentRequest = TestUtils.defaultUpdateDiscountPercentRequest();
        Long promocodeId = updateDiscountPercentRequest.getPromocodeId();
        String exceptionMessage = String.format(MessageTemplates.PROMOCODE_NOT_FOUND_BY_ID.getValue(), promocodeId);

        when(promocodeRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> promocodeService.updatePromocode(updateDiscountPercentRequest))
                .isInstanceOf(PromocodeNotFoundException.class)
                .hasMessage(exceptionMessage);
        verify(promocodeRepository, never()).save(any());
    }

    @Test
    void deletePromocode_ValidPromocodeId_ShouldDeletePromocode() {
        Long promocodeId = TestConstants.PROMOCODE_ID;
        Promocode promocode = TestUtils.defaultPromocode();

        when(promocodeRepository.findById(anyLong()))
                .thenReturn(Optional.of(promocode));
        doNothing().when(promocodeRepository)
                .delete(any(Promocode.class));

        assertDoesNotThrow(() -> promocodeService.deletePromocode(promocodeId));

        verify(promocodeRepository).findById(promocodeId);
        verify(promocodeRepository).delete(promocode);
    }

    @Test
    void deletePromocode_PromocodeDoesNotExist_ThrowPromocodeNotFoundException() {
        Long promocodeId = TestConstants.PROMOCODE_ID;
        String exceptionMessage = String.format(MessageTemplates.PROMOCODE_NOT_FOUND_BY_ID.getValue(), promocodeId);

        when(promocodeRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> promocodeService.deletePromocode(promocodeId))
                .isInstanceOf(PromocodeNotFoundException.class)
                .hasMessage(exceptionMessage);
        verify(promocodeRepository, never()).delete(any());
    }
}