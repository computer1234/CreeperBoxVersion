package helper.creeperbox.sdk.network.packet.modify;

import org.cloudburstmc.protocol.bedrock.packet.StartGamePacket;

import java.util.Arrays;
import java.util.List;

public class NeteaseStartGamePacket extends StartGamePacket {

    @Override
    public NeteaseStartGamePacket clone() {
        return (NeteaseStartGamePacket) super.clone();
    }

    private byte[] extra;

    private boolean hardcore;

    private String serverId;
    private String worldId;
    private String scenarioId;


    public String getScenarioId() {
        return scenarioId;
    }

    public String getServerId() {
        return serverId;
    }

    public String getWorldId() {
        return worldId;
    }

    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public void setWorldId(String worldId) {
        this.worldId = worldId;
    }

    public byte[] getExtra() {
        return extra;
    }

    public void setExtra(byte[] extra) {
        this.extra = extra;
    }

    public boolean isHardcore() {
        return hardcore;
    }

    public void setHardcore(boolean hardcore) {
        this.hardcore = hardcore;
    }


    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof NeteaseStartGamePacket)) {
            return false;
        } else {
            NeteaseStartGamePacket other = (NeteaseStartGamePacket)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getUniqueEntityId() != other.getUniqueEntityId()) {
                return false;
            } else if (this.getRuntimeEntityId() != other.getRuntimeEntityId()) {
                return false;
            } else if (this.getSeed() != other.getSeed()) {
                return false;
            } else if (this.getDimensionId() != other.getDimensionId()) {
                return false;
            } else if (this.getGeneratorId() != other.getGeneratorId()) {
                return false;
            } else if (this.getDifficulty() != other.getDifficulty()) {
                return false;
            } else if (this.isAchievementsDisabled() != other.isAchievementsDisabled()) {
                return false;
            } else if (this.getDayCycleStopTime() != other.getDayCycleStopTime()) {
                return false;
            } else if (this.getEduEditionOffers() != other.getEduEditionOffers()) {
                return false;
            } else if (this.isEduFeaturesEnabled() != other.isEduFeaturesEnabled()) {
                return false;
            } else if (Float.compare(this.getRainLevel(), other.getRainLevel()) != 0) {
                return false;
            } else if (Float.compare(this.getLightningLevel(), other.getLightningLevel()) != 0) {
                return false;
            } else if (this.isPlatformLockedContentConfirmed() != other.isPlatformLockedContentConfirmed()) {
                return false;
            } else if (this.isMultiplayerGame() != other.isMultiplayerGame()) {
                return false;
            } else if (this.isBroadcastingToLan() != other.isBroadcastingToLan()) {
                return false;
            } else if (this.isCommandsEnabled() != other.isCommandsEnabled()) {
                return false;
            } else if (this.isTexturePacksRequired() != other.isTexturePacksRequired()) {
                return false;
            } else if (this.isExperimentsPreviouslyToggled() != other.isExperimentsPreviouslyToggled()) {
                return false;
            } else if (this.isBonusChestEnabled() != other.isBonusChestEnabled()) {
                return false;
            } else if (this.isStartingWithMap() != other.isStartingWithMap()) {
                return false;
            } else if (this.isTrustingPlayers() != other.isTrustingPlayers()) {
                return false;
            } else if (this.getServerChunkTickRange() != other.getServerChunkTickRange()) {
                return false;
            } else if (this.isBehaviorPackLocked() != other.isBehaviorPackLocked()) {
                return false;
            } else if (this.isResourcePackLocked() != other.isResourcePackLocked()) {
                return false;
            } else if (this.isFromLockedWorldTemplate() != other.isFromLockedWorldTemplate()) {
                return false;
            } else if (this.isUsingMsaGamertagsOnly() != other.isUsingMsaGamertagsOnly()) {
                return false;
            } else if (this.isFromWorldTemplate() != other.isFromWorldTemplate()) {
                return false;
            } else if (this.isWorldTemplateOptionLocked() != other.isWorldTemplateOptionLocked()) {
                return false;
            } else if (this.isOnlySpawningV1Villagers() != other.isOnlySpawningV1Villagers()) {
                return false;
            } else if (this.getLimitedWorldWidth() != other.getLimitedWorldWidth()) {
                return false;
            } else if (this.getLimitedWorldHeight() != other.getLimitedWorldHeight()) {
                return false;
            } else if (this.isNetherType() != other.isNetherType()) {
                return false;
            } else if (this.isDisablingPlayerInteractions() != other.isDisablingPlayerInteractions()) {
                return false;
            } else if (this.isDisablingPersonas() != other.isDisablingPersonas()) {
                return false;
            } else if (this.isDisablingCustomSkins() != other.isDisablingCustomSkins()) {
                return false;
            } else if (this.isTrial() != other.isTrial()) {
                return false;
            } else if (this.getRewindHistorySize() != other.getRewindHistorySize()) {
                return false;
            } else if (this.isServerAuthoritativeBlockBreaking() != other.isServerAuthoritativeBlockBreaking()) {
                return false;
            } else if (this.getCurrentTick() != other.getCurrentTick()) {
                return false;
            } else if (this.getEnchantmentSeed() != other.getEnchantmentSeed()) {
                return false;
            } else if (this.isInventoriesServerAuthoritative() != other.isInventoriesServerAuthoritative()) {
                return false;
            } else if (this.getBlockRegistryChecksum() != other.getBlockRegistryChecksum()) {
                return false;
            } else if (this.isClientSideGenerationEnabled() != other.isClientSideGenerationEnabled()) {
                return false;
            } else if (this.isEmoteChatMuted() != other.isEmoteChatMuted()) {
                return false;
            } else if (this.isBlockNetworkIdsHashed() != other.isBlockNetworkIdsHashed()) {
                return false;
            } else if (this.isCreatedInEditor() != other.isCreatedInEditor()) {
                return false;
            } else if (this.isExportedFromEditor() != other.isExportedFromEditor()) {
                return false;
            } else if (this.isHardcore() != other.isHardcore()) {
                return false;
            } else {
                Object this$gamerules = this.getGamerules();
                Object other$gamerules = other.getGamerules();
                if (this$gamerules == null) {
                    if (other$gamerules != null) {
                        return false;
                    }
                } else if (!this$gamerules.equals(other$gamerules)) {
                    return false;
                }

                Object this$playerGameType = this.getPlayerGameType();
                Object other$playerGameType = other.getPlayerGameType();
                if (this$playerGameType == null) {
                    if (other$playerGameType != null) {
                        return false;
                    }
                } else if (!this$playerGameType.equals(other$playerGameType)) {
                    return false;
                }

                label471: {
                    Object this$playerPosition = this.getPlayerPosition();
                    Object other$playerPosition = other.getPlayerPosition();
                    if (this$playerPosition == null) {
                        if (other$playerPosition == null) {
                            break label471;
                        }
                    } else if (this$playerPosition.equals(other$playerPosition)) {
                        break label471;
                    }

                    return false;
                }

                label464: {
                    Object this$rotation = this.getRotation();
                    Object other$rotation = other.getRotation();
                    if (this$rotation == null) {
                        if (other$rotation == null) {
                            break label464;
                        }
                    } else if (this$rotation.equals(other$rotation)) {
                        break label464;
                    }

                    return false;
                }

                Object this$spawnBiomeType = this.getSpawnBiomeType();
                Object other$spawnBiomeType = other.getSpawnBiomeType();
                if (this$spawnBiomeType == null) {
                    if (other$spawnBiomeType != null) {
                        return false;
                    }
                } else if (!this$spawnBiomeType.equals(other$spawnBiomeType)) {
                    return false;
                }

                Object this$customBiomeName = this.getCustomBiomeName();
                Object other$customBiomeName = other.getCustomBiomeName();
                if (this$customBiomeName == null) {
                    if (other$customBiomeName != null) {
                        return false;
                    }
                } else if (!this$customBiomeName.equals(other$customBiomeName)) {
                    return false;
                }

                label443: {
                    Object this$levelGameType = this.getLevelGameType();
                    Object other$levelGameType = other.getLevelGameType();
                    if (this$levelGameType == null) {
                        if (other$levelGameType == null) {
                            break label443;
                        }
                    } else if (this$levelGameType.equals(other$levelGameType)) {
                        break label443;
                    }

                    return false;
                }

                Object this$defaultSpawn = this.getDefaultSpawn();
                Object other$defaultSpawn = other.getDefaultSpawn();
                if (this$defaultSpawn == null) {
                    if (other$defaultSpawn != null) {
                        return false;
                    }
                } else if (!this$defaultSpawn.equals(other$defaultSpawn)) {
                    return false;
                }

                Object this$educationProductionId = this.getEducationProductionId();
                Object other$educationProductionId = other.getEducationProductionId();
                if (this$educationProductionId == null) {
                    if (other$educationProductionId != null) {
                        return false;
                    }
                } else if (!this$educationProductionId.equals(other$educationProductionId)) {
                    return false;
                }

                label422: {
                    Object this$xblBroadcastMode = this.getXblBroadcastMode();
                    Object other$xblBroadcastMode = other.getXblBroadcastMode();
                    if (this$xblBroadcastMode == null) {
                        if (other$xblBroadcastMode == null) {
                            break label422;
                        }
                    } else if (this$xblBroadcastMode.equals(other$xblBroadcastMode)) {
                        break label422;
                    }

                    return false;
                }

                label415: {
                    Object this$platformBroadcastMode = this.getPlatformBroadcastMode();
                    Object other$platformBroadcastMode = other.getPlatformBroadcastMode();
                    if (this$platformBroadcastMode == null) {
                        if (other$platformBroadcastMode == null) {
                            break label415;
                        }
                    } else if (this$platformBroadcastMode.equals(other$platformBroadcastMode)) {
                        break label415;
                    }

                    return false;
                }

                label408: {
                    Object this$experiments = this.getExperiments();
                    Object other$experiments = other.getExperiments();
                    if (this$experiments == null) {
                        if (other$experiments == null) {
                            break label408;
                        }
                    } else if (this$experiments.equals(other$experiments)) {
                        break label408;
                    }

                    return false;
                }

                Object this$defaultPlayerPermission = this.getDefaultPlayerPermission();
                Object other$defaultPlayerPermission = other.getDefaultPlayerPermission();
                if (this$defaultPlayerPermission == null) {
                    if (other$defaultPlayerPermission != null) {
                        return false;
                    }
                } else if (!this$defaultPlayerPermission.equals(other$defaultPlayerPermission)) {
                    return false;
                }

                label394: {
                    Object this$vanillaVersion = this.getVanillaVersion();
                    Object other$vanillaVersion = other.getVanillaVersion();
                    if (this$vanillaVersion == null) {
                        if (other$vanillaVersion == null) {
                            break label394;
                        }
                    } else if (this$vanillaVersion.equals(other$vanillaVersion)) {
                        break label394;
                    }

                    return false;
                }

                Object this$eduSharedUriResource = this.getEduSharedUriResource();
                Object other$eduSharedUriResource = other.getEduSharedUriResource();
                if (this$eduSharedUriResource == null) {
                    if (other$eduSharedUriResource != null) {
                        return false;
                    }
                } else if (!this$eduSharedUriResource.equals(other$eduSharedUriResource)) {
                    return false;
                }

                label380: {
                    Object this$forceExperimentalGameplay = this.getForceExperimentalGameplay();
                    Object other$forceExperimentalGameplay = other.getForceExperimentalGameplay();
                    if (this$forceExperimentalGameplay == null) {
                        if (other$forceExperimentalGameplay == null) {
                            break label380;
                        }
                    } else if (this$forceExperimentalGameplay.equals(other$forceExperimentalGameplay)) {
                        break label380;
                    }

                    return false;
                }

                Object this$chatRestrictionLevel = this.getChatRestrictionLevel();
                Object other$chatRestrictionLevel = other.getChatRestrictionLevel();
                if (this$chatRestrictionLevel == null) {
                    if (other$chatRestrictionLevel != null) {
                        return false;
                    }
                } else if (!this$chatRestrictionLevel.equals(other$chatRestrictionLevel)) {
                    return false;
                }

                Object this$levelId = this.getLevelId();
                Object other$levelId = other.getLevelId();
                if (this$levelId == null) {
                    if (other$levelId != null) {
                        return false;
                    }
                } else if (!this$levelId.equals(other$levelId)) {
                    return false;
                }

                label359: {
                    Object this$levelName = this.getLevelName();
                    Object other$levelName = other.getLevelName();
                    if (this$levelName == null) {
                        if (other$levelName == null) {
                            break label359;
                        }
                    } else if (this$levelName.equals(other$levelName)) {
                        break label359;
                    }

                    return false;
                }

                label352: {
                    Object this$premiumWorldTemplateId = this.getPremiumWorldTemplateId();
                    Object other$premiumWorldTemplateId = other.getPremiumWorldTemplateId();
                    if (this$premiumWorldTemplateId == null) {
                        if (other$premiumWorldTemplateId == null) {
                            break label352;
                        }
                    } else if (this$premiumWorldTemplateId.equals(other$premiumWorldTemplateId)) {
                        break label352;
                    }

                    return false;
                }

                Object this$authoritativeMovementMode = this.getAuthoritativeMovementMode();
                Object other$authoritativeMovementMode = other.getAuthoritativeMovementMode();
                if (this$authoritativeMovementMode == null) {
                    if (other$authoritativeMovementMode != null) {
                        return false;
                    }
                } else if (!this$authoritativeMovementMode.equals(other$authoritativeMovementMode)) {
                    return false;
                }

                Object this$blockPalette = this.getBlockPalette();
                Object other$blockPalette = other.getBlockPalette();
                if (this$blockPalette == null) {
                    if (other$blockPalette != null) {
                        return false;
                    }
                } else if (!this$blockPalette.equals(other$blockPalette)) {
                    return false;
                }

                label331: {
                    Object this$blockProperties = this.getBlockProperties();
                    Object other$blockProperties = other.getBlockProperties();
                    if (this$blockProperties == null) {
                        if (other$blockProperties == null) {
                            break label331;
                        }
                    } else if (this$blockProperties.equals(other$blockProperties)) {
                        break label331;
                    }

                    return false;
                }

                Object this$itemDefinitions = this.getItemDefinitions();
                Object other$itemDefinitions = other.getItemDefinitions();
                if (this$itemDefinitions == null) {
                    if (other$itemDefinitions != null) {
                        return false;
                    }
                } else if (!this$itemDefinitions.equals(other$itemDefinitions)) {
                    return false;
                }

                Object this$multiplayerCorrelationId = this.getMultiplayerCorrelationId();
                Object other$multiplayerCorrelationId = other.getMultiplayerCorrelationId();
                if (this$multiplayerCorrelationId == null) {
                    if (other$multiplayerCorrelationId != null) {
                        return false;
                    }
                } else if (!this$multiplayerCorrelationId.equals(other$multiplayerCorrelationId)) {
                    return false;
                }

                label310: {
                    Object this$serverEngine = this.getServerEngine();
                    Object other$serverEngine = other.getServerEngine();
                    if (this$serverEngine == null) {
                        if (other$serverEngine == null) {
                            break label310;
                        }
                    } else if (this$serverEngine.equals(other$serverEngine)) {
                        break label310;
                    }

                    return false;
                }

                label303: {
                    Object this$playerPropertyData = this.getPlayerPropertyData();
                    Object other$playerPropertyData = other.getPlayerPropertyData();
                    if (this$playerPropertyData == null) {
                        if (other$playerPropertyData == null) {
                            break label303;
                        }
                    } else if (this$playerPropertyData.equals(other$playerPropertyData)) {
                        break label303;
                    }

                    return false;
                }

                label296: {
                    Object this$worldTemplateId = this.getWorldTemplateId();
                    Object other$worldTemplateId = other.getWorldTemplateId();
                    if (this$worldTemplateId == null) {
                        if (other$worldTemplateId == null) {
                            break label296;
                        }
                    } else if (this$worldTemplateId.equals(other$worldTemplateId)) {
                        break label296;
                    }

                    return false;
                }

                label289: {
                    Object this$networkPermissions = this.getNetworkPermissions();
                    Object other$networkPermissions = other.getNetworkPermissions();
                    if (this$networkPermissions == null) {
                        if (other$networkPermissions == null) {
                            break label289;
                        }
                    } else if (this$networkPermissions.equals(other$networkPermissions)) {
                        break label289;
                    }

                    return false;
                }

                if (!Arrays.equals(this.getExtra(), other.getExtra())) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof StartGamePacket;
    }

    public int hashCode() {
        int result = 1;
        long $uniqueEntityId = this.getUniqueEntityId();
        result = result * 59 + (int)($uniqueEntityId >>> 32 ^ $uniqueEntityId);
        long $runtimeEntityId = this.getRuntimeEntityId();
        result = result * 59 + (int)($runtimeEntityId >>> 32 ^ $runtimeEntityId);
        long $seed = this.getSeed();
        result = result * 59 + (int)($seed >>> 32 ^ $seed);
        result = result * 59 + this.getDimensionId();
        result = result * 59 + this.getGeneratorId();
        result = result * 59 + this.getDifficulty();
        result = result * 59 + (this.isAchievementsDisabled() ? 79 : 97);
        result = result * 59 + this.getDayCycleStopTime();
        result = result * 59 + this.getEduEditionOffers();
        result = result * 59 + (this.isEduFeaturesEnabled() ? 79 : 97);
        result = result * 59 + Float.floatToIntBits(this.getRainLevel());
        result = result * 59 + Float.floatToIntBits(this.getLightningLevel());
        result = result * 59 + (this.isPlatformLockedContentConfirmed() ? 79 : 97);
        result = result * 59 + (this.isMultiplayerGame() ? 79 : 97);
        result = result * 59 + (this.isBroadcastingToLan() ? 79 : 97);
        result = result * 59 + (this.isCommandsEnabled() ? 79 : 97);
        result = result * 59 + (this.isTexturePacksRequired() ? 79 : 97);
        result = result * 59 + (this.isExperimentsPreviouslyToggled() ? 79 : 97);
        result = result * 59 + (this.isBonusChestEnabled() ? 79 : 97);
        result = result * 59 + (this.isStartingWithMap() ? 79 : 97);
        result = result * 59 + (this.isTrustingPlayers() ? 79 : 97);
        result = result * 59 + this.getServerChunkTickRange();
        result = result * 59 + (this.isBehaviorPackLocked() ? 79 : 97);
        result = result * 59 + (this.isResourcePackLocked() ? 79 : 97);
        result = result * 59 + (this.isFromLockedWorldTemplate() ? 79 : 97);
        result = result * 59 + (this.isUsingMsaGamertagsOnly() ? 79 : 97);
        result = result * 59 + (this.isFromWorldTemplate() ? 79 : 97);
        result = result * 59 + (this.isWorldTemplateOptionLocked() ? 79 : 97);
        result = result * 59 + (this.isOnlySpawningV1Villagers() ? 79 : 97);
        result = result * 59 + this.getLimitedWorldWidth();
        result = result * 59 + this.getLimitedWorldHeight();
        result = result * 59 + (this.isNetherType() ? 79 : 97);
        result = result * 59 + (this.isDisablingPlayerInteractions() ? 79 : 97);
        result = result * 59 + (this.isDisablingPersonas() ? 79 : 97);
        result = result * 59 + (this.isDisablingCustomSkins() ? 79 : 97);
        result = result * 59 + (this.isTrial() ? 79 : 97);
        result = result * 59 + this.getRewindHistorySize();
        result = result * 59 + (this.isServerAuthoritativeBlockBreaking() ? 79 : 97);
        long $currentTick = this.getCurrentTick();
        result = result * 59 + (int)($currentTick >>> 32 ^ $currentTick);
        result = result * 59 + this.getEnchantmentSeed();
        result = result * 59 + (this.isInventoriesServerAuthoritative() ? 79 : 97);
        long $blockRegistryChecksum = this.getBlockRegistryChecksum();
        result = result * 59 + (int)($blockRegistryChecksum >>> 32 ^ $blockRegistryChecksum);
        result = result * 59 + (this.isClientSideGenerationEnabled() ? 79 : 97);
        result = result * 59 + (this.isEmoteChatMuted() ? 79 : 97);
        result = result * 59 + (this.isBlockNetworkIdsHashed() ? 79 : 97);
        result = result * 59 + (this.isCreatedInEditor() ? 79 : 97);
        result = result * 59 + (this.isExportedFromEditor() ? 79 : 97);
        result = result * 59 + (this.isHardcore() ? 79 : 97);
        Object $gamerules = this.getGamerules();
        result = result * 59 + ($gamerules == null ? 43 : $gamerules.hashCode());
        Object $playerGameType = this.getPlayerGameType();
        result = result * 59 + ($playerGameType == null ? 43 : $playerGameType.hashCode());
        Object $playerPosition = this.getPlayerPosition();
        result = result * 59 + ($playerPosition == null ? 43 : $playerPosition.hashCode());
        Object $rotation = this.getRotation();
        result = result * 59 + ($rotation == null ? 43 : $rotation.hashCode());
        Object $spawnBiomeType = this.getSpawnBiomeType();
        result = result * 59 + ($spawnBiomeType == null ? 43 : $spawnBiomeType.hashCode());
        Object $customBiomeName = this.getCustomBiomeName();
        result = result * 59 + ($customBiomeName == null ? 43 : $customBiomeName.hashCode());
        Object $levelGameType = this.getLevelGameType();
        result = result * 59 + ($levelGameType == null ? 43 : $levelGameType.hashCode());
        Object $defaultSpawn = this.getDefaultSpawn();
        result = result * 59 + ($defaultSpawn == null ? 43 : $defaultSpawn.hashCode());
        Object $educationProductionId = this.getEducationProductionId();
        result = result * 59 + ($educationProductionId == null ? 43 : $educationProductionId.hashCode());
        Object $xblBroadcastMode = this.getXblBroadcastMode();
        result = result * 59 + ($xblBroadcastMode == null ? 43 : $xblBroadcastMode.hashCode());
        Object $platformBroadcastMode = this.getPlatformBroadcastMode();
        result = result * 59 + ($platformBroadcastMode == null ? 43 : $platformBroadcastMode.hashCode());
        Object $experiments = this.getExperiments();
        result = result * 59 + ($experiments == null ? 43 : $experiments.hashCode());
        Object $defaultPlayerPermission = this.getDefaultPlayerPermission();
        result = result * 59 + ($defaultPlayerPermission == null ? 43 : $defaultPlayerPermission.hashCode());
        Object $vanillaVersion = this.getVanillaVersion();
        result = result * 59 + ($vanillaVersion == null ? 43 : $vanillaVersion.hashCode());
        Object $eduSharedUriResource = this.getEduSharedUriResource();
        result = result * 59 + ($eduSharedUriResource == null ? 43 : $eduSharedUriResource.hashCode());
        Object $forceExperimentalGameplay = this.getForceExperimentalGameplay();
        result = result * 59 + ($forceExperimentalGameplay == null ? 43 : $forceExperimentalGameplay.hashCode());
        Object $chatRestrictionLevel = this.getChatRestrictionLevel();
        result = result * 59 + ($chatRestrictionLevel == null ? 43 : $chatRestrictionLevel.hashCode());
        Object $levelId = this.getLevelId();
        result = result * 59 + ($levelId == null ? 43 : $levelId.hashCode());
        Object $levelName = this.getLevelName();
        result = result * 59 + ($levelName == null ? 43 : $levelName.hashCode());
        Object $premiumWorldTemplateId = this.getPremiumWorldTemplateId();
        result = result * 59 + ($premiumWorldTemplateId == null ? 43 : $premiumWorldTemplateId.hashCode());
        Object $authoritativeMovementMode = this.getAuthoritativeMovementMode();
        result = result * 59 + ($authoritativeMovementMode == null ? 43 : $authoritativeMovementMode.hashCode());
        Object $blockPalette = this.getBlockPalette();
        result = result * 59 + ($blockPalette == null ? 43 : $blockPalette.hashCode());
        Object $blockProperties = this.getBlockProperties();
        result = result * 59 + ($blockProperties == null ? 43 : $blockProperties.hashCode());
        Object $itemDefinitions = this.getItemDefinitions();
        result = result * 59 + ($itemDefinitions == null ? 43 : $itemDefinitions.hashCode());
        Object $multiplayerCorrelationId = this.getMultiplayerCorrelationId();
        result = result * 59 + ($multiplayerCorrelationId == null ? 43 : $multiplayerCorrelationId.hashCode());
        Object $serverEngine = this.getServerEngine();
        result = result * 59 + ($serverEngine == null ? 43 : $serverEngine.hashCode());
        Object $playerPropertyData = this.getPlayerPropertyData();
        result = result * 59 + ($playerPropertyData == null ? 43 : $playerPropertyData.hashCode());
        Object $worldTemplateId = this.getWorldTemplateId();
        result = result * 59 + ($worldTemplateId == null ? 43 : $worldTemplateId.hashCode());
        Object $networkPermissions = this.getNetworkPermissions();
        result = result * 59 + ($networkPermissions == null ? 43 : $networkPermissions.hashCode());
        result = result * 59 + Arrays.hashCode(this.getExtra());
        return result;
    }

    public String toString() {
        List var10000 = this.getGamerules();
        return "NeteaseStartGamePacket(gamerules=" + var10000 + ", uniqueEntityId=" + this.getUniqueEntityId() + ", runtimeEntityId=" + this.getRuntimeEntityId() + ", playerGameType=" + this.getPlayerGameType() + ", playerPosition=" + this.getPlayerPosition() + ", rotation=" + this.getRotation() + ", seed=" + this.getSeed() + ", spawnBiomeType=" + this.getSpawnBiomeType() + ", customBiomeName=" + this.getCustomBiomeName() + ", dimensionId=" + this.getDimensionId() + ", generatorId=" + this.getGeneratorId() + ", levelGameType=" + this.getLevelGameType() + ", difficulty=" + this.getDifficulty() + ", defaultSpawn=" + this.getDefaultSpawn() + ", achievementsDisabled=" + this.isAchievementsDisabled() + ", dayCycleStopTime=" + this.getDayCycleStopTime() + ", eduEditionOffers=" + this.getEduEditionOffers() + ", eduFeaturesEnabled=" + this.isEduFeaturesEnabled() + ", educationProductionId=" + this.getEducationProductionId() + ", rainLevel=" + this.getRainLevel() + ", lightningLevel=" + this.getLightningLevel() + ", platformLockedContentConfirmed=" + this.isPlatformLockedContentConfirmed() + ", multiplayerGame=" + this.isMultiplayerGame() + ", broadcastingToLan=" + this.isBroadcastingToLan() + ", xblBroadcastMode=" + this.getXblBroadcastMode() + ", platformBroadcastMode=" + this.getPlatformBroadcastMode() + ", commandsEnabled=" + this.isCommandsEnabled() + ", texturePacksRequired=" + this.isTexturePacksRequired() + ", experiments=" + this.getExperiments() + ", experimentsPreviouslyToggled=" + this.isExperimentsPreviouslyToggled() + ", bonusChestEnabled=" + this.isBonusChestEnabled() + ", startingWithMap=" + this.isStartingWithMap() + ", trustingPlayers=" + this.isTrustingPlayers() + ", defaultPlayerPermission=" + this.getDefaultPlayerPermission() + ", serverChunkTickRange=" + this.getServerChunkTickRange() + ", behaviorPackLocked=" + this.isBehaviorPackLocked() + ", resourcePackLocked=" + this.isResourcePackLocked() + ", fromLockedWorldTemplate=" + this.isFromLockedWorldTemplate() + ", usingMsaGamertagsOnly=" + this.isUsingMsaGamertagsOnly() + ", fromWorldTemplate=" + this.isFromWorldTemplate() + ", worldTemplateOptionLocked=" + this.isWorldTemplateOptionLocked() + ", onlySpawningV1Villagers=" + this.isOnlySpawningV1Villagers() + ", vanillaVersion=" + this.getVanillaVersion() + ", limitedWorldWidth=" + this.getLimitedWorldWidth() + ", limitedWorldHeight=" + this.getLimitedWorldHeight() + ", netherType=" + this.isNetherType() + ", eduSharedUriResource=" + this.getEduSharedUriResource() + ", forceExperimentalGameplay=" + this.getForceExperimentalGameplay() + ", chatRestrictionLevel=" + this.getChatRestrictionLevel() + ", disablingPlayerInteractions=" + this.isDisablingPlayerInteractions() + ", disablingPersonas=" + this.isDisablingPersonas() + ", disablingCustomSkins=" + this.isDisablingCustomSkins() + ", levelId=" + this.getLevelId() + ", levelName=" + this.getLevelName() + ", premiumWorldTemplateId=" + this.getPremiumWorldTemplateId() + ", trial=" + this.isTrial() + ", authoritativeMovementMode=" + this.getAuthoritativeMovementMode() + ", rewindHistorySize=" + this.getRewindHistorySize() + ", serverAuthoritativeBlockBreaking=" + this.isServerAuthoritativeBlockBreaking() + ", currentTick=" + this.getCurrentTick() + ", enchantmentSeed=" + this.getEnchantmentSeed() + ", blockProperties=" + this.getBlockProperties() + ", multiplayerCorrelationId=" + this.getMultiplayerCorrelationId() + ", inventoriesServerAuthoritative=" + this.isInventoriesServerAuthoritative() + ", serverEngine=" + this.getServerEngine() + ", playerPropertyData=" + this.getPlayerPropertyData() + ", blockRegistryChecksum=" + this.getBlockRegistryChecksum() + ", worldTemplateId=" + this.getWorldTemplateId()  + ", clientSideGenerationEnabled=" + this.isClientSideGenerationEnabled() + ", emoteChatMuted=" + this.isEmoteChatMuted() + ", blockNetworkIdsHashed=" + this.isBlockNetworkIdsHashed() + ", createdInEditor=" + this.isCreatedInEditor() + ", exportedFromEditor=" + this.isExportedFromEditor() + ", networkPermissions=" + this.getNetworkPermissions() + ", hardcore=" + this.isHardcore() + ", temp=" + Arrays.toString(this.getExtra()) + ")";
    }


}
