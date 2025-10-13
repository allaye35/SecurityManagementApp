import React, { useState, useEffect, useMemo } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  Container, Grid, TextField, Typography, Button, Box, Paper, 
  Autocomplete, FormControl, InputLabel, Select, MenuItem,
  Divider, Alert, Snackbar, CircularProgress, InputAdornment,
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow
} from '@mui/material';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { LocalizationProvider, DatePicker } from '@mui/x-date-pickers';
import { fr } from 'date-fns/locale';
import MissionService from '../../services/MissionService';
import FactureService from '../../services/FactureService';
import JoursFeriesService from '../../services/JoursFeriesService';

const FacturationMission = () => {
  const [mission, setMission] = useState(null);
  const [facture, setFacture] = useState({
    referenceFacture: '',
    dateEmission: new Date(),
    montantHT: 0,
    montantTVA: 0,
    montantTTC: 0,
    missionIds: [],
    devisId: null
  });
  
  const [joursFeries, setJoursFeries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: '',
    severity: 'success'
  });
  
  const { id } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        
        const missionResponse = await MissionService.getById(id);
        const missionData = missionResponse.data;
        setMission(missionData);
        
        setFacture(prev => ({
          ...prev,
          missionIds: [missionData.id],
          devisId: missionData.devisId,
          montantHT: missionData.montantHT || 0,
          montantTVA: missionData.montantTVA || 0,
          montantTTC: missionData.montantTTC || 0
        }));
        
        const anneeDebut = new Date(missionData.dateDebut).getFullYear();
        const anneeFin = new Date(missionData.dateFin).getFullYear();
        
        const years = new Set([anneeDebut, anneeFin]);
        const joursFeriesPromises = [...years].map(year => 
          JoursFeriesService.getByAnnee(year)
        );
        
        const joursFeriesResponses = await Promise.all(joursFeriesPromises);
        const allJoursFeries = joursFeriesResponses.flatMap(res => res.data);
        setJoursFeries(allJoursFeries);
        
        setLoading(false);
      } catch (err) {
        console.error('Erreur lors du chargement des données:', err);
        setError('Erreur lors du chargement des données. Veuillez réessayer.');
        setLoading(false);
      }
    };

    fetchData();
  }, [id]);

  const calculerMontants = () => {
    if (!mission || !mission.tarif) return;

    const tarif = mission.tarif;
    const dateDebut = new Date(mission.dateDebut + 'T' + mission.heureDebut);
    const dateFin = new Date(mission.dateFin + 'T' + mission.heureFin);
    
    const durationMs = dateFin - dateDebut;
    const durationHours = Math.ceil(durationMs / (1000 * 60 * 60));
    
    let heuresNormales = 0;
    let heuresNuit = 0;
    let heuresWeekend = 0;
    let heuresDimanche = 0;
    let heuresFerie = 0;
    
    let currentDate = new Date(dateDebut);
    while (currentDate < dateFin) {
      const nextHour = new Date(currentDate);
      nextHour.setHours(nextHour.getHours() + 1);
      
      const dateToCheck = new Date(currentDate);
      const isoDate = dateToCheck.toISOString().split('T')[0];
      const time = currentDate.getHours();
      const dayOfWeek = currentDate.getDay();
      
      if (joursFeries.includes(isoDate)) {
        heuresFerie++;
      } else if (dayOfWeek === 0) {
        heuresDimanche++;
      } else if (dayOfWeek === 6) {
        heuresWeekend++;
      } else if (time >= 22 || time < 6) {
        heuresNuit++;
      } else {
        heuresNormales++;
      }
      
      currentDate = nextHour;
    }
    
    const prixUnitaire = tarif.prixUnitaireHT;
    const montantNormal = prixUnitaire * heuresNormales;
    const montantNuit = prixUnitaire * (1 + tarif.majorationNuit) * heuresNuit;
    const montantWeekend = prixUnitaire * (1 + tarif.majorationWeekend) * heuresWeekend;
    const montantDimanche = prixUnitaire * (1 + tarif.majorationDimanche) * heuresDimanche;
    const montantFerie = prixUnitaire * (1 + tarif.majorationFerie) * heuresFerie;
    
    const totalHT = (montantNormal + montantNuit + montantWeekend + montantDimanche + montantFerie) * 
                     mission.nombreAgents * mission.quantite;
    
    const montantTVA = totalHT * tarif.tauxTVA;
    const montantTTC = totalHT + montantTVA;
    
    setFacture(prev => ({
      ...prev,
      montantHT: parseFloat(totalHT.toFixed(2)),
      montantTVA: parseFloat(montantTVA.toFixed(2)),
      montantTTC: parseFloat(montantTTC.toFixed(2))
    }));
  };

  useEffect(() => {
    if (mission && joursFeries.length > 0) {
      calculerMontants();
    }
  }, [mission, joursFeries]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFacture(prev => ({ ...prev, [name]: value }));
  };

  const handleDateChange = (date) => {
    setFacture(prev => ({ ...prev, dateEmission: date }));
  };

  const isFormValid = useMemo(() => {
    return (
      facture.referenceFacture && 
      facture.dateEmission &&
      facture.montantHT > 0 &&
      facture.montantTVA >= 0 &&
      facture.montantTTC > 0 &&
      facture.missionIds.length > 0 &&
      facture.devisId
    );
  }, [facture]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      setLoading(true);
      
      const response = await FactureService.create(facture);
      const newFactureId = response.data.id;
      
      await MissionService.associerFacture(id, newFactureId);
      
      setSnackbar({
        open: true,
        message: 'Facture enregistrée avec succès',
        severity: 'success'
      });
      
      setTimeout(() => {
        navigate(`/factures/${newFactureId}`);
      }, 2000);
      
    } catch (err) {
      console.error('Erreur lors de la création de la facture:', err);
      setSnackbar({
        open: true,
        message: 'Erreur lors de la création de la facture. Veuillez réessayer.',
        severity: 'error'
      });
      setLoading(false);
    }
  };

  const handleCloseSnackbar = () => {
    setSnackbar(prev => ({ ...prev, open: false }));
  };

  if (loading && !mission) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="80vh">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="80vh">
        <Alert severity="error">{error}</Alert>
      </Box>
    );
  }

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={fr}>
      <Container maxWidth="lg">
        <Paper elevation={3} sx={{ padding: 3, mt: 4, mb: 4 }}>
          <Typography variant="h4" component="h1" gutterBottom>
            Facturation de la mission
          </Typography>
          
          {mission && (
            <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 3 }}>
              {}
              <Paper variant="outlined" sx={{ p: 2, mb: 3 }}>
                <Typography variant="h6" gutterBottom>
                  Détails de la mission #{mission.id}
                </Typography>
                
                <Grid container spacing={2}>
                  <Grid item xs={12} sm={6}>
                    <Typography variant="subtitle1">
                      <strong>Titre:</strong> {mission.titre}
                    </Typography>
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <Typography variant="subtitle1">
                      <strong>Type:</strong> {mission.typeMission}
                    </Typography>
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <Typography variant="subtitle1">
                      <strong>Date début:</strong> {mission.dateDebut} à {mission.heureDebut}
                    </Typography>
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <Typography variant="subtitle1">
                      <strong>Date fin:</strong> {mission.dateFin} à {mission.heureFin}
                    </Typography>
                  </Grid>
                  <Grid item xs={12} sm={4}>
                    <Typography variant="subtitle1">
                      <strong>Nombre d'agents:</strong> {mission.nombreAgents}
                    </Typography>
                  </Grid>
                  <Grid item xs={12} sm={4}>
                    <Typography variant="subtitle1">
                      <strong>Quantité:</strong> {mission.quantite}
                    </Typography>
                  </Grid>
                  <Grid item xs={12} sm={4}>
                    <Typography variant="subtitle1">
                      <strong>Tarif unitaire HT:</strong> {mission.tarif?.prixUnitaireHT} €
                    </Typography>
                  </Grid>
                </Grid>
              </Paper>
              
              {}
              <Paper variant="outlined" sx={{ p: 2, mb: 3 }}>
                <Typography variant="h6" gutterBottom>
                  Tarifs et majorations applicables
                </Typography>
                
                <TableContainer>
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell><strong>Type</strong></TableCell>
                        <TableCell align="right"><strong>Taux (%)</strong></TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      <TableRow>
                        <TableCell>Majoration nuit (22h-6h)</TableCell>
                        <TableCell align="right">{mission.tarif?.majorationNuit * 100}%</TableCell>
                      </TableRow>
                      <TableRow>
                        <TableCell>Majoration weekend (samedi)</TableCell>
                        <TableCell align="right">{mission.tarif?.majorationWeekend * 100}%</TableCell>
                      </TableRow>
                      <TableRow>
                        <TableCell>Majoration dimanche</TableCell>
                        <TableCell align="right">{mission.tarif?.majorationDimanche * 100}%</TableCell>
                      </TableRow>
                      <TableRow>
                        <TableCell>Majoration jour férié</TableCell>
                        <TableCell align="right">{mission.tarif?.majorationFerie * 100}%</TableCell>
                      </TableRow>
                      <TableRow>
                        <TableCell>TVA</TableCell>
                        <TableCell align="right">{mission.tarif?.tauxTVA * 100}%</TableCell>
                      </TableRow>
                    </TableBody>
                  </Table>
                </TableContainer>
              </Paper>

              {}
              <Grid container spacing={3}>
                <Grid item xs={12} sm={6}>
                  <TextField
                    required
                    fullWidth
                    label="Référence facture"
                    name="referenceFacture"
                    value={facture.referenceFacture}
                    onChange={handleInputChange}
                    error={!facture.referenceFacture}
                    helperText={!facture.referenceFacture ? "La référence est obligatoire" : ""}
                  />
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <DatePicker
                    label="Date d'émission"
                    value={facture.dateEmission}
                    onChange={handleDateChange}
                    renderInput={(params) => <TextField {...params} fullWidth required />}
                  />
                </Grid>
                
                <Grid item xs={12}>
                  <Divider sx={{ my: 2 }} />
                  <Typography variant="h6" gutterBottom>
                    Montants calculés
                  </Typography>
                </Grid>
                
                <Grid item xs={12} sm={4}>
                  <TextField
                    fullWidth
                    label="Montant HT"
                    name="montantHT"
                    value={facture.montantHT}
                    InputProps={{
                      readOnly: true,
                      endAdornment: <InputAdornment position="end">€</InputAdornment>,
                    }}
                  />
                </Grid>
                
                <Grid item xs={12} sm={4}>
                  <TextField
                    fullWidth
                    label="Montant TVA"
                    name="montantTVA"
                    value={facture.montantTVA}
                    InputProps={{
                      readOnly: true,
                      endAdornment: <InputAdornment position="end">€</InputAdornment>,
                    }}
                  />
                </Grid>
                
                <Grid item xs={12} sm={4}>
                  <TextField
                    fullWidth
                    label="Montant TTC"
                    name="montantTTC"
                    value={facture.montantTTC}
                    InputProps={{
                      readOnly: true,
                      endAdornment: <InputAdornment position="end">€</InputAdornment>,
                    }}
                  />
                </Grid>
              </Grid>
              
              <Box sx={{ mt: 4, display: 'flex', justifyContent: 'space-between' }}>
                <Button 
                  variant="outlined" 
                  onClick={() => navigate(-1)}
                >
                  Annuler
                </Button>
                <Button 
                  type="submit" 
                  variant="contained" 
                  color="primary"
                  disabled={!isFormValid || loading}
                >
                  {loading ? <CircularProgress size={24} /> : 'Enregistrer la facture'}
                </Button>
              </Box>
            </Box>
          )}
        </Paper>
        
        <Snackbar 
          open={snackbar.open} 
          autoHideDuration={6000} 
          onClose={handleCloseSnackbar}
          anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
        >
          <Alert 
            onClose={handleCloseSnackbar} 
            severity={snackbar.severity} 
            variant="filled"
            sx={{ width: '100%' }}
          >
            {snackbar.message}
          </Alert>
        </Snackbar>
      </Container>
    </LocalizationProvider>
  );
};

export default FacturationMission;