package mvg.solo.team;

import mvg.solo.util.Colour;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeamTest {
    private static final Team japan = new Team("JAPAN", 70,
            Colour.RED, Colour.BLUE);
    private static final Team southAfrica = new Team("SOUTH AFRICA", 90,
            Colour.GREEN, Colour.CYAN);

    @Mock Team mockTeam;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTeams() {
    }

    @Test
    void getTeam() {
//        MockedStatic<Team> staticMockTeam = spy(Team.class);
//        staticMockTeam.when(Team::getTeams).thenReturn(
//                Map.of("JAPAN", japan));
//        Team newJapan = Team.getTeam("JAPAN");
//        assertEquals(newJapan, japan);
//        staticMockTeam.verify(Team::getTeams, only());
    }

    @Test
    void getRatingPoints() {
//        when(mockCreator.readRatingPoints(mockTeam)).thenReturn(80.0);
//        double ratingPoints = mockTeam.getRatingPoints();
//        assertEquals(80.0, ratingPoints);
//        verify(mockCreator, only()).readRatingPoints(mockTeam);
    }

    @Test
    void updateRatingPoints() {
    }

    @Test
    void testToString() {
        assertEquals("South Africa", southAfrica.toString());
        assertEquals("Japan", japan.toString());
    }

    @Test
    void homeKitString() {
        assertEquals(Colour.RED + "Japan" + Colour.RESET,
                japan.homeKitString());
    }

    @Test
    void alternateKitString() {
        assertEquals(Colour.BLUE + "Japan" + Colour.RESET,
                japan.alternateKitString());
    }

    @Test
    void countryName() {
        assertEquals("JAPAN", japan.countryName());
    }

    @Test
    void initialRatingPoints() {
        assertEquals(70, japan.initialRatingPoints());
    }

    @Test
    void homeKit() {
        assertEquals(Colour.RED, japan.homeKit());
    }

    @Test
    void alternateKit() {
        assertEquals(Colour.BLUE, japan.alternateKit());
    }
}