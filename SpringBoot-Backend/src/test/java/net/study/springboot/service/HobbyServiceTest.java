package net.study.springboot.service;

import net.study.springboot.domain.Hobby;
import net.study.springboot.exception.DataFormatException;
import net.study.springboot.exception.ResourceAlreadyExistException;
import net.study.springboot.exception.ResourceNotFoundException;
import net.study.springboot.helper.HobbyModelConverter;
import net.study.springboot.model.HobbyModel;
import net.study.springboot.repository.HobbyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HobbyServiceTest {

    @Mock
    private HobbyRepository hobbyRepository;

    @Mock
    private HobbyModelConverter hobbyModelConverter;

    @InjectMocks
    private HobbyService hobbyService;

    private HobbyModel validHobbyModel;
    private Hobby validHobby;

    @BeforeEach
    void setUp() {
        validHobbyModel = HobbyModel.builder()
                .Id(1)
                .name("Reading")
                .description("Enjoying books and literature")
                .build();

        validHobby = new Hobby();
        validHobby.setId(1);
        validHobby.setName("Reading");
        validHobby.setDescription("Enjoying books and literature");
    }

    // Individual Validation Function Tests

    @Test
    void validateAllParameters_validHobby_returnsTrue() {
        assertTrue(hobbyService.validateAllParameters(validHobbyModel));
    }

    @Test
    void validateAllParameters_nullName_returnsFalse() {
        HobbyModel hobbyWithNullName = HobbyModel.builder()
                .Id(1)
                .name(null)
                .description("Valid description")
                .build();

        assertFalse(hobbyService.validateAllParameters(hobbyWithNullName));
    }

    @Test
    void validateAllParameters_nullDescription_returnsFalse() {
        HobbyModel hobbyWithNullDescription = HobbyModel.builder()
                .Id(1)
                .name("Valid name")
                .description(null)
                .build();

        assertFalse(hobbyService.validateAllParameters(hobbyWithNullDescription));
    }

    @Test
    void validateAllParameters_emptyName_returnsFalse() {
        HobbyModel hobbyWithEmptyName = HobbyModel.builder()
                .Id(1)
                .name("")
                .description("Valid description")
                .build();

        assertFalse(hobbyService.validateAllParameters(hobbyWithEmptyName));
    }

    @Test
    void validateAllParameters_emptyDescription_returnsFalse() {
        HobbyModel hobbyWithEmptyDescription = HobbyModel.builder()
                .Id(1)
                .name("Valid name")
                .description("")
                .build();

        assertFalse(hobbyService.validateAllParameters(hobbyWithEmptyDescription));
    }

    @Test
    void validateAllParameters_bothNameAndDescriptionNull_returnsFalse() {
        HobbyModel hobbyWithNulls = HobbyModel.builder()
                .Id(1)
                .name(null)
                .description(null)
                .build();

        assertFalse(hobbyService.validateAllParameters(hobbyWithNulls));
    }

    @Test
    void validateAllParameters_bothNameAndDescriptionEmpty_returnsFalse() {
        HobbyModel hobbyWithEmpty = HobbyModel.builder()
                .Id(1)
                .name("")
                .description("")
                .build();

        assertFalse(hobbyService.validateAllParameters(hobbyWithEmpty));
    }

    @Test
    void validateAllParameters_whitespaceOnlyName_returnsTrue() {
        HobbyModel hobbyWithWhitespaceName = HobbyModel.builder()
                .Id(1)
                .name("   ")
                .description("Valid description")
                .build();

        assertTrue(hobbyService.validateAllParameters(hobbyWithWhitespaceName));
    }

    @Test
    void validateAllParameters_whitespaceOnlyDescription_returnsTrue() {
        HobbyModel hobbyWithWhitespaceDescription = HobbyModel.builder()
                .Id(1)
                .name("Valid name")
                .description("   ")
                .build();

        assertTrue(hobbyService.validateAllParameters(hobbyWithWhitespaceDescription));
    }

    @Test
    void validateAllParameters_singleCharacterFields_returnsTrue() {
        HobbyModel hobbyWithSingleChars = HobbyModel.builder()
                .Id(1)
                .name("A")
                .description("B")
                .build();

        assertTrue(hobbyService.validateAllParameters(hobbyWithSingleChars));
    }

    @Test
    void validateAllParameters_veryLongFields_returnsTrue() {
        String longName = "A".repeat(1000);
        String longDescription = "B".repeat(1000);

        HobbyModel hobbyWithLongFields = HobbyModel.builder()
                .Id(1)
                .name(longName)
                .description(longDescription)
                .build();

        assertTrue(hobbyService.validateAllParameters(hobbyWithLongFields));
    }

    @Test
    void validateAllParameters_specialCharacters_returnsTrue() {
        HobbyModel hobbyWithSpecialChars = HobbyModel.builder()
                .Id(1)
                .name("Rock & Roll!")
                .description("Playing rock music with guitars & drums!")
                .build();

        assertTrue(hobbyService.validateAllParameters(hobbyWithSpecialChars));
    }

    @Test
    void validateAllParameters_numericFields_returnsTrue() {
        HobbyModel hobbyWithNumbers = HobbyModel.builder()
                .Id(1)
                .name("Formula 1 Racing")
                .description("Following Formula 1 races from 1950 to present")
                .build();

        assertTrue(hobbyService.validateAllParameters(hobbyWithNumbers));
    }

    // Service Method Tests

    @Test
    void getHobbyById_validId_success() {
        when(hobbyRepository.findById(anyInt())).thenReturn(Optional.of(validHobby));
        when(hobbyModelConverter.convertHobbytoHobbyModel(any())).thenReturn(validHobbyModel);

        HobbyModel result = hobbyService.getHobbyById(1);

        assertNotNull(result);
        assertEquals(validHobbyModel.getName(), result.getName());
        verify(hobbyRepository).findById(1);
        verify(hobbyModelConverter).convertHobbytoHobbyModel(validHobby);
    }

    @Test
    void getHobbyById_hobbyNotFound_throwsException() {
        when(hobbyRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> hobbyService.getHobbyById(1));

        verify(hobbyRepository).findById(1);
        verify(hobbyModelConverter, never()).convertHobbytoHobbyModel(any());
    }

    @Test
    void getHobbyById_zeroId_throwsException() {
        when(hobbyRepository.findById(0)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> hobbyService.getHobbyById(0));
    }

    @Test
    void getHobbyById_negativeId_throwsException() {
        when(hobbyRepository.findById(-1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> hobbyService.getHobbyById(-1));
    }

    @Test
    void addHobbyService_validHobby_success() {
        when(hobbyRepository.existsById(anyInt())).thenReturn(false);
        when(hobbyRepository.existsByName(anyString())).thenReturn(false);
        when(hobbyModelConverter.convertHobbyModeltoHobby(any())).thenReturn(validHobby);
        when(hobbyRepository.findIdByName(anyString())).thenReturn(1);

        HobbyModel result = hobbyService.addHobbyService(validHobbyModel);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(hobbyRepository).save(validHobby);
        verify(hobbyRepository).findIdByName(validHobbyModel.getName());
    }

    @Test
    void addHobbyService_invalidData_throwsDataFormatException() {
        HobbyModel invalidHobby = HobbyModel.builder()
                .Id(1)
                .name("")
                .description("Valid description")
                .build();

        assertThrows(DataFormatException.class,
            () -> hobbyService.addHobbyService(invalidHobby));

        verify(hobbyRepository, never()).save(any());
    }

    @Test
    void addHobbyService_hobbyExistsById_throwsResourceAlreadyExistException() {
        when(hobbyRepository.existsById(anyInt())).thenReturn(true);

        assertThrows(ResourceAlreadyExistException.class,
            () -> hobbyService.addHobbyService(validHobbyModel));

        verify(hobbyRepository, never()).save(any());
    }

    @Test
    void addHobbyService_hobbyExistsByName_throwsResourceAlreadyExistException() {
        when(hobbyRepository.existsById(anyInt())).thenReturn(false);
        when(hobbyRepository.existsByName(anyString())).thenReturn(true);

        assertThrows(ResourceAlreadyExistException.class,
            () -> hobbyService.addHobbyService(validHobbyModel));

        verify(hobbyRepository, never()).save(any());
    }

    @Test
    void addHobbyService_nullHobbyModel_throwsDataFormatException() {
        assertThrows(DataFormatException.class,
            () -> hobbyService.addHobbyService(null));
    }

    @Test
    void hobbyUpdateService_validUpdate_success() {
        when(hobbyRepository.existsById(anyInt())).thenReturn(true);
        when(hobbyRepository.existsByNameExcludingId(anyString(), anyInt())).thenReturn(false);
        when(hobbyModelConverter.convertHobbyModeltoHobby(any())).thenReturn(validHobby);

        HobbyModel result = hobbyService.hobbyUpdateService(validHobbyModel, 1);

        assertNotNull(result);
        verify(hobbyRepository).save(validHobby);
    }

    @Test
    void hobbyUpdateService_invalidData_throwsDataFormatException() {
        HobbyModel invalidHobby = HobbyModel.builder()
                .Id(1)
                .name("")
                .description("Valid description")
                .build();

        assertThrows(DataFormatException.class,
            () -> hobbyService.hobbyUpdateService(invalidHobby, 1));

        verify(hobbyRepository, never()).save(any());
    }

    @Test
    void hobbyUpdateService_idMismatch_throwsDataFormatException() {
        assertThrows(DataFormatException.class,
            () -> hobbyService.hobbyUpdateService(validHobbyModel, 2));

        verify(hobbyRepository, never()).save(any());
    }

    @Test
    void hobbyUpdateService_hobbyNotFound_throwsResourceNotFoundException() {
        when(hobbyRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> hobbyService.hobbyUpdateService(validHobbyModel, 1));

        verify(hobbyRepository, never()).save(any());
    }

    @Test
    void hobbyUpdateService_nameAlreadyExists_throwsResourceAlreadyExistException() {
        when(hobbyRepository.existsById(anyInt())).thenReturn(true);
        when(hobbyRepository.existsByNameExcludingId(anyString(), anyInt())).thenReturn(true);

        assertThrows(ResourceAlreadyExistException.class,
            () -> hobbyService.hobbyUpdateService(validHobbyModel, 1));

        verify(hobbyRepository, never()).save(any());
    }

    @Test
    void getAllHobbyService_success() {
        List<Hobby> hobbies = Arrays.asList(validHobby);
        when(hobbyRepository.findAll()).thenReturn(hobbies);

        List<HobbyModel> result = hobbyService.getAllHobbyService();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(validHobby.getName(), result.get(0).getName());
        verify(hobbyRepository).findAll();
    }

    @Test
    void getAllHobbyService_emptyList_returnsEmptyList() {
        when(hobbyRepository.findAll()).thenReturn(Arrays.asList());

        List<HobbyModel> result = hobbyService.getAllHobbyService();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(hobbyRepository).findAll();
    }

    @Test
    void getAllHobbyService_multipleHobbies_returnsAllHobbies() {
        Hobby hobby1 = new Hobby();
        hobby1.setId(1);
        hobby1.setName("Reading");
        hobby1.setDescription("Books");

        Hobby hobby2 = new Hobby();
        hobby2.setId(2);
        hobby2.setName("Swimming");
        hobby2.setDescription("Water sports");

        List<Hobby> hobbies = Arrays.asList(hobby1, hobby2);
        when(hobbyRepository.findAll()).thenReturn(hobbies);

        List<HobbyModel> result = hobbyService.getAllHobbyService();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Reading", result.get(0).getName());
        assertEquals("Swimming", result.get(1).getName());
        verify(hobbyRepository).findAll();
    }

    @Test
    void deleteHobbyService_validId_success() {
        when(hobbyRepository.existsById(anyInt())).thenReturn(true);

        assertDoesNotThrow(() -> hobbyService.deleteHobbyService(1));

        verify(hobbyRepository).deleteById(1);
    }

    @Test
    void deleteHobbyService_hobbyNotFound_throwsException() {
        when(hobbyRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> hobbyService.deleteHobbyService(1));

        verify(hobbyRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteHobbyService_zeroId_throwsException() {
        when(hobbyRepository.existsById(0)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> hobbyService.deleteHobbyService(0));

        verify(hobbyRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteHobbyService_negativeId_throwsException() {
        when(hobbyRepository.existsById(-1)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> hobbyService.deleteHobbyService(-1));

        verify(hobbyRepository, never()).deleteById(anyInt());
    }

    // Edge Cases and Boundary Conditions

    @Test
    void addHobbyService_withSpecialCharacters_success() {
        HobbyModel specialCharHobby = HobbyModel.builder()
                .Id(1)
                .name("Rock & Roll!")
                .description("Playing music with guitars & drums!")
                .build();

        when(hobbyRepository.existsById(anyInt())).thenReturn(false);
        when(hobbyRepository.existsByName(anyString())).thenReturn(false);
        when(hobbyModelConverter.convertHobbyModeltoHobby(any())).thenReturn(validHobby);
        when(hobbyRepository.findIdByName(anyString())).thenReturn(1);

        HobbyModel result = hobbyService.addHobbyService(specialCharHobby);

        assertNotNull(result);
        verify(hobbyRepository).save(any());
    }

    @Test
    void addHobbyService_withNumericCharacters_success() {
        HobbyModel numericHobby = HobbyModel.builder()
                .Id(1)
                .name("Formula 1 Racing")
                .description("Following Formula 1 races from 1950 to present")
                .build();

        when(hobbyRepository.existsById(anyInt())).thenReturn(false);
        when(hobbyRepository.existsByName(anyString())).thenReturn(false);
        when(hobbyModelConverter.convertHobbyModeltoHobby(any())).thenReturn(validHobby);
        when(hobbyRepository.findIdByName(anyString())).thenReturn(1);

        HobbyModel result = hobbyService.addHobbyService(numericHobby);

        assertNotNull(result);
        verify(hobbyRepository).save(any());
    }

    @Test
    void addHobbyService_withVeryLongName_success() {
        String longName = "A".repeat(500);
        HobbyModel longNameHobby = HobbyModel.builder()
                .Id(1)
                .name(longName)
                .description("Short description")
                .build();

        when(hobbyRepository.existsById(anyInt())).thenReturn(false);
        when(hobbyRepository.existsByName(anyString())).thenReturn(false);
        when(hobbyModelConverter.convertHobbyModeltoHobby(any())).thenReturn(validHobby);
        when(hobbyRepository.findIdByName(anyString())).thenReturn(1);

        HobbyModel result = hobbyService.addHobbyService(longNameHobby);

        assertNotNull(result);
        verify(hobbyRepository).save(any());
    }

    @Test
    void addHobbyService_withVeryLongDescription_success() {
        String longDescription = "B".repeat(1000);
        HobbyModel longDescHobby = HobbyModel.builder()
                .Id(1)
                .name("Short name")
                .description(longDescription)
                .build();

        when(hobbyRepository.existsById(anyInt())).thenReturn(false);
        when(hobbyRepository.existsByName(anyString())).thenReturn(false);
        when(hobbyModelConverter.convertHobbyModeltoHobby(any())).thenReturn(validHobby);
        when(hobbyRepository.findIdByName(anyString())).thenReturn(1);

        HobbyModel result = hobbyService.addHobbyService(longDescHobby);

        assertNotNull(result);
        verify(hobbyRepository).save(any());
    }

    @Test
    void hobbyUpdateService_updateToSameName_success() {
        when(hobbyRepository.existsById(anyInt())).thenReturn(true);
        when(hobbyRepository.existsByNameExcludingId(anyString(), anyInt())).thenReturn(false);
        when(hobbyModelConverter.convertHobbyModeltoHobby(any())).thenReturn(validHobby);

        HobbyModel result = hobbyService.hobbyUpdateService(validHobbyModel, 1);

        assertNotNull(result);
        verify(hobbyRepository).save(validHobby);
    }

    @Test
    void hobbyUpdateService_withMinimalValidData_success() {
        HobbyModel minimalHobby = HobbyModel.builder()
                .Id(1)
                .name("A")
                .description("B")
                .build();

        when(hobbyRepository.existsById(anyInt())).thenReturn(true);
        when(hobbyRepository.existsByNameExcludingId(anyString(), anyInt())).thenReturn(false);
        when(hobbyModelConverter.convertHobbyModeltoHobby(any())).thenReturn(validHobby);

        HobbyModel result = hobbyService.hobbyUpdateService(minimalHobby, 1);

        assertNotNull(result);
        verify(hobbyRepository).save(validHobby);
    }

    @Test
    void deleteHobbyService_largeId_throwsException() {
        int largeId = Integer.MAX_VALUE;
        when(hobbyRepository.existsById(largeId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> hobbyService.deleteHobbyService(largeId));

        verify(hobbyRepository, never()).deleteById(anyInt());
    }

    @Test
    void getAllHobbyService_withNullFields_handlesGracefully() {
        Hobby hobbyWithNulls = new Hobby();
        hobbyWithNulls.setId(1);
        hobbyWithNulls.setName(null);
        hobbyWithNulls.setDescription(null);

        List<Hobby> hobbies = Arrays.asList(hobbyWithNulls);
        when(hobbyRepository.findAll()).thenReturn(hobbies);

        List<HobbyModel> result = hobbyService.getAllHobbyService();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getName());
        assertNull(result.get(0).getDescription());
    }

    @Test
    void hobbyUpdateService_withZeroId_throwsDataFormatException() {
        HobbyModel hobbyWithZeroId = HobbyModel.builder()
                .Id(0)
                .name("Valid name")
                .description("Valid description")
                .build();

        assertThrows(DataFormatException.class,
            () -> hobbyService.hobbyUpdateService(hobbyWithZeroId, 1));

        verify(hobbyRepository, never()).save(any());
    }

    @Test
    void addHobbyService_verifyIdIsSetAfterSave() {
        when(hobbyRepository.existsById(anyInt())).thenReturn(false);
        when(hobbyRepository.existsByName(anyString())).thenReturn(false);
        when(hobbyModelConverter.convertHobbyModeltoHobby(any())).thenReturn(validHobby);
        when(hobbyRepository.findIdByName("Reading")).thenReturn(42);

        HobbyModel result = hobbyService.addHobbyService(validHobbyModel);

        assertEquals(42, result.getId());
        verify(hobbyRepository).findIdByName("Reading");
        verify(hobbyRepository).save(validHobby);
    }
}