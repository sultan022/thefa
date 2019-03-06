package com.thefa.audit.model.kind;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "PlayerStatsCaps")
public class PlayerStatsCapsKind {

    @Id
    private String id;
    private String playerId;
    private Integer u16Caps;
    private Integer u17Caps;
    private Integer u18Caps;
    private Integer u19Caps;
    private Integer u20Caps;
    private Integer u21Caps;
    private Integer seniorCaps;
    private Integer appearancesPrevSeas;
    private Integer startsPrevSeas;
    private Integer minutesPlayedPrevSeas;
    private String minutesPlayedRatingPrevSeas;
    private Integer championsLeagueAppPrevSeas;
    private Integer europeLeagueAppPrevSeas;
    private Integer premierLeagueAppPrevSeas;
    private Integer championshipAppPrevSeas;
    private Integer league1AppPrevSeas;
    private Integer league2AppPrevSeas;
    private Integer faCupAppPrevSeas;
    private Integer eflCupAppPrevSeas;
    private Integer eflTrophyAppPrevSeas;
    private Integer u23AppPrevSeas;
    private Integer u18plAppPrevSeas;
    private Integer u16AppPrevSeas;
    private Integer u14AppPrevSeas;
    private Integer faYouthCupAppPrevSeas;
    private Integer uefaYouthLeaguePrevSeas;
    private Integer foreignLeagueAppPrevSeas;
    private Integer appearances;
    private Integer starts;
    private Integer minutesPlayed;
    private String minutesPlayedRating;
    private Integer championsLeageAppearances;
    private Integer europeLeagueAppearances;
    private Integer premierLeagueAppearances;
    private Integer championshipAppearances;
    private Integer league1Appearances;
    private Integer league2Appearances;
    private Integer faCupAppearances;
    private Integer eflCupAppearances;
    private Integer eflTrophyAppearances;
    private Integer u23Appearances;
    private Integer pdfAppearances;
    private Integer u18plAppearances;
    private Integer u16Appearances;
    private Integer u14Appearances;
    private Integer faYouthCupAppearances;
    private Integer uefaYouthLeague;
    private Integer foreignLeagueAppearances;

}
