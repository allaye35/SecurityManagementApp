import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import "./styles/Layout.css";

import ProtectedRoute from "./routes/ProtectedRoute";
import AppLayout from "./components/layout/AppLayout";

import Home from "./pages/Home";
import LoginPage from "./pages/LoginPage";
import Unauthorized from "./pages/Unauthorized";

import RegisterChooserPage from "./pages/RegisterChooserPage";
import RegisterClientPage from "./pages/RegisterClientPage";
import RegisterAgentPage from "./pages/RegisterAgentPage";

import VerifyEmailPage from "./pages/VerifyEmailPage";
import VerifyEmailCodePage from "./pages/VerifyEmailCodePage";
import VerifyEmailResendPage from "./pages/VerifyEmailResendPage";
import PasswordResetRequestPage from "./pages/PasswordResetRequestPage";
import PasswordResetConfirmPage from "./pages/PasswordResetConfirmPage";

import PointageList from "./components/pointages/PointageList";
import PointageForm from "./components/pointages/PointageForm";
import PointageDetail from "./components/pointages/PointageDetail";

import AgentList from "./components/agents/AgentList";
import AgentCreate from "./components/agents/CreateAgent";
import AgentEdit from "./components/agents/EditAgent";
import AgentDetail from "./components/agents/AgentDetail";
import DeleteAgent from "./components/agents/DeleteAgent";

import EntrepriseList from "./components/entreprises/EntrepriseList";
import CreateEntreprise from "./components/entreprises/CreateEntreprise";
import EditEntreprise from "./components/entreprises/EditEntreprise";
import EntrepriseDetail from "./components/entreprises/EntrepriseDetail";
import DeleteEntreprise from "./components/entreprises/DeleteEntreprise";

import GeolocalisationList from "./components/geolocalisations/GeolocalisationList";
import CreateGeolocalisation from "./components/geolocalisations/CreateGeolocalisation";
import EditGeolocalisation from "./components/geolocalisations/EditGeolocalisation";
import GeolocalisationDetail from "./components/geolocalisations/GeolocalisationDetail";
import DeleteGeolocalisation from "./components/geolocalisations/DeleteGeolocalisation";

import MissionList from "./components/missions/MissionList";
import CreateMission from "./components/missions/CreateMission";
import EditMission from "./components/missions/EditMission";
import MissionDetail from "./components/missions/MissionDetail";
import DeleteMission from "./components/missions/DeleteMission";
import AssignMissionRelations from "./components/missions/AssignMissionRelations";

import PlanningList from "./components/plannings/PlanningList";
import CreatePlanning from "./components/plannings/CreatePlanning";
import EditPlanning from "./components/plannings/EditPlanning";
import PlanningDetail from "./components/plannings/PlanningDetail";
import DeletePlanning from "./components/plannings/DeletePlanning";

import SiteList from "./components/sites/SiteList";
import CreateSite from "./components/sites/CreateSite";
import EditSite from "./components/sites/EditSite";
import SiteDetail from "./components/sites/SiteDetail";
import DeleteSite from "./components/sites/DeleteSite";

import RapportList from "./components/rapports/RapportList";
import CreateRapport from "./components/rapports/CreateRapport";
import EditRapport from "./components/rapports/EditRapport";
import RapportDetail from "./components/rapports/RapportDetail";
import DeleteRapport from "./components/rapports/DeleteRapport";

import ClientList from "./components/clients/ClientList";
import CreateClient from "./components/clients/CreateClient";
import EditClient from "./components/clients/EditClient";
import ClientDetail from "./components/clients/ClientDetail";
import DeleteClient from "./components/clients/DeleteClient";

import ArticleList from "./components/articles/ArticleList";
import CreateArticle from "./components/articles/CreateArticle";
import EditArticle from "./components/articles/EditArticle";
import ArticleDetail from "./components/articles/ArticleDetail";

import ZoneList from "./components/zones/ZoneList";
import ZoneCreate from "./components/zones/ZoneCreate";
import ZoneEdit from "./components/zones/ZoneEdit";
import ZoneDetail from "./components/zones/ZoneDetail";

import DisponibiliteList from "./components/disponibilites/DisponibiliteList";
import DisponibiliteCreate from "./components/disponibilites/DisponibiliteCreate";
import DisponibiliteEdit from "./components/disponibilites/DisponibiliteEdit";
import DisponibiliteDetail from "./components/disponibilites/DisponibiliteDetail";

import CarteProList from "./components/cartesPro/CarteProList";
import CarteProCreate from "./components/cartesPro/CarteProCreate";
import CarteProEdit from "./components/cartesPro/CarteProEdit";
import CarteProDetail from "./components/cartesPro/CarteProDetail";

import DiplomeList from "./components/diplomes/DiplomeList";
import DiplomeCreate from "./components/diplomes/DiplomeCreate";
import DiplomeEdit from "./components/diplomes/DiplomeEdit";
import DiplomeDetail from "./components/diplomes/DiplomeDetail";

import NotificationList from "./components/notifications/NotificationList";
import NotificationCreate from "./components/notifications/NotificationCreate";
import NotificationEdit from "./components/notifications/NotificationEdit";

import ContratDeTravailList from "./components/contrats-de-travail/ContratDeTravailList";
import ContratDeTravailCreate from "./components/contrats-de-travail/ContratDeTravailCreate";
import ContratDeTravailEdit from "./components/contrats-de-travail/ContratDeTravailEdit";
import ContratDeTravailDetail from "./components/contrats-de-travail/ContratDeTravailDetail";

import DevisList from "./components/devis/DevisList";
import DevisForm from "./components/devis/DevisForm";
import DevisDetail from "./components/devis/DevisDetail";

import FactureList from "./components/factures/FactureList";
import FactureForm from "./components/factures/FactureForm";
import FactureDetail from "./components/factures/FactureDetail";
import FacturePrint from "./components/factures/FacturePrint";

import FicheDePaieList from "./components/ficheDePaie/FicheDePaieList";
import FicheDePaieForm from "./components/ficheDePaie/FicheDePaieForm";
import FicheDePaieDetail from "./components/ficheDePaie/FicheDePaieDetail";

import ArticleContratTravailList from "./components/articleContratTravails/ArticleContratTravailList";
import ArticleContratTravailForm from "./components/articleContratTravails/ArticleContratTravailForm";
import ArticleContratTravailView from "./components/articleContratTravails/ArticleContratTravailView";

import ContratList from "./components/contrats/ContratList";
import ContratDetail from "./components/contrats/ContratDetail";
import CreateContrat from "./components/contrats/CreateContrat";
import EditContrat from "./components/contrats/EditContrat";

import LigneCotisationList from "./components/lignesCotisation/LigneCotisationList";
import LigneCotisationForm from "./components/lignesCotisation/LigneCotisationForm";
import LigneCotisationDetail from "./components/lignesCotisation/LigneCotisationDetail";

import TarifMissionList from "./components/tarifs/TarifMissionList";
import TarifMissionForm from "./components/tarifs/TarifMissionForm";
import TarifMissionDetail from "./components/tarifs/TarifMissionDetail";

import AdminPendingAccounts from "./pages/AdminPendingAccounts";
import AdminUserManagement from "./pages/AdminUserManagement";

import "leaflet/dist/leaflet.css";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {}
        <Route path="/" element={<Navigate to="/login" replace />} />

        {}
        <Route path="/login" element={<LoginPage />} />
        {}
        <Route path="/register" element={<RegisterChooserPage />} />
        <Route path="/register/agent" element={<RegisterAgentPage />} />
        <Route path="/register/client" element={<RegisterClientPage />} />

        {}
        <Route path="/verify-email" element={<VerifyEmailPage />} />
        <Route path="/verify-email/code" element={<VerifyEmailCodePage />} />
        <Route path="/verify-email/resend" element={<VerifyEmailResendPage />} />

        {}
        <Route path="/password-reset/request" element={<PasswordResetRequestPage />} />
        <Route path="/password-reset/confirm" element={<PasswordResetConfirmPage />} />

        <Route path="/unauthorized" element={<Unauthorized />} />

        {}
        <Route element={<ProtectedRoute />}>
          <Route element={<AppLayout />}>
            <Route path="/home" element={<Home />} />

            {}
            <Route element={<ProtectedRoute roles={['ADMIN']} />}>
              <Route path="/admin/pending-accounts" element={<AdminPendingAccounts />} />
              <Route path="/admin/users" element={<AdminUserManagement />} />
            </Route>

            {}
            <Route path="/agents" element={<AgentList />} />
            <Route path="/agents/create" element={<AgentCreate />} />
            <Route path="/agents/edit/:id" element={<AgentEdit />} />
            <Route path="/agents/delete/:id" element={<DeleteAgent />} />
            <Route path="/agents/:id" element={<AgentDetail />} />

            {}
            <Route path="/entreprises" element={<EntrepriseList />} />
            <Route path="/entreprises/create" element={<CreateEntreprise />} />
            <Route path="/entreprises/edit/:id" element={<EditEntreprise />} />
            <Route path="/entreprises/delete/:id" element={<DeleteEntreprise />} />
            <Route path="/entreprises/:id" element={<EntrepriseDetail />} />

            {}
            <Route path="/geolocalisations" element={<GeolocalisationList />} />
            <Route path="/geolocalisations/create" element={<CreateGeolocalisation />} />
            <Route path="/geolocalisations/edit/:id" element={<EditGeolocalisation />} />
            <Route path="/geolocalisations/delete/:id" element={<DeleteGeolocalisation />} />
            <Route path="/geolocalisations/:id" element={<GeolocalisationDetail />} />

            {}
            <Route path="/missions" element={<MissionList />} />
            <Route path="/missions/create" element={<CreateMission />} />
            <Route path="/missions/edit/:id" element={<EditMission />} />
            <Route path="/missions/delete/:id" element={<DeleteMission />} />
            <Route path="/missions/:id" element={<MissionDetail />} />
            <Route path="/missions/:id/assign" element={<AssignMissionRelations />} />

            {}
            <Route path="/plannings" element={<PlanningList />} />
            <Route path="/plannings/create" element={<CreatePlanning />} />
            <Route path="/plannings/edit/:id" element={<EditPlanning />} />
            <Route path="/plannings/delete/:id" element={<DeletePlanning />} />
            <Route path="/plannings/:id" element={<PlanningDetail />} />

            {}
            <Route path="/sites" element={<SiteList />} />
            <Route path="/sites/create" element={<CreateSite />} />
            <Route path="/sites/edit/:id" element={<EditSite />} />
            <Route path="/sites/delete/:id" element={<DeleteSite />} />
            <Route path="/sites/:id" element={<SiteDetail />} />

            {}
            <Route path="/pointages" element={<PointageList />} />
            <Route path="/pointages/create" element={<PointageForm />} />
            <Route path="/pointages/edit/:id" element={<PointageForm />} />
            <Route path="/pointages/:id" element={<PointageDetail />} />

            {}
            <Route path="/rapports" element={<RapportList />} />
            <Route path="/rapports/create" element={<CreateRapport />} />
            <Route path="/rapports/edit/:id" element={<EditRapport />} />
            <Route path="/rapports/delete/:id" element={<DeleteRapport />} />
            <Route path="/rapports/:id" element={<RapportDetail />} />

            {}
            <Route path="/clients" element={<ClientList />} />
            <Route path="/clients/create" element={<CreateClient />} />
            <Route path="/clients/edit/:id" element={<EditClient />} />
            <Route path="/clients/delete/:id" element={<DeleteClient />} />
            <Route path="/clients/:id" element={<ClientDetail />} />

            {}
            <Route path="/articles" element={<ArticleList />} />
            <Route path="/articles/create" element={<CreateArticle />} />
            <Route path="/articles/edit/:id" element={<EditArticle />} />
            <Route path="/articles/:id" element={<ArticleDetail />} />

            {}
            <Route path="/zones" element={<ZoneList />} />
            <Route path="/zones/create" element={<ZoneCreate />} />
            <Route path="/zones/edit/:id" element={<ZoneEdit />} />
            <Route path="/zones/:id" element={<ZoneDetail />} />

            {}
            <Route path="/disponibilites" element={<DisponibiliteList />} />
            <Route path="/disponibilites/create" element={<DisponibiliteCreate />} />
            <Route path="/disponibilites/edit/:id" element={<DisponibiliteEdit />} />
            <Route path="/disponibilites/:id" element={<DisponibiliteDetail />} />

            {}
            <Route path="/cartes-professionnelles" element={<CarteProList />} />
            <Route path="/cartes-professionnelles/create" element={<CarteProCreate />} />
            <Route path="/cartes-professionnelles/edit/:id" element={<CarteProEdit />} />
            <Route path="/cartes-professionnelles/:id" element={<CarteProDetail />} />

            {}
            <Route path="/diplomes-ssiap" element={<DiplomeList />} />
            <Route path="/diplomes-ssiap/create" element={<DiplomeCreate />} />
            <Route path="/diplomes-ssiap/edit/:id" element={<DiplomeEdit />} />
            <Route path="/diplomes-ssiap/:id" element={<DiplomeDetail />} />

            {}
            <Route path="/notifications" element={<NotificationList />} />
            <Route path="/notifications/create" element={<NotificationCreate />} />
            <Route path="/notifications/edit/:id" element={<NotificationEdit />} />

            {}
            <Route path="/contrats-de-travail" element={<ContratDeTravailList />} />
            <Route path="/contrats-de-travail/create" element={<ContratDeTravailCreate />} />
            <Route path="/contrats-de-travail/edit/:id" element={<ContratDeTravailEdit />} />
            <Route path="/contrats-de-travail/:id" element={<ContratDeTravailDetail />} />

            {}
            <Route path="/devis" element={<DevisList />} />
            <Route path="/devis/create" element={<DevisForm />} />
            <Route path="/devis/edit/:id" element={<DevisForm />} />
            <Route path="/devis/:id" element={<DevisDetail />} />

            <Route path="/factures" element={<FactureList />} />
            <Route path="/factures/create" element={<FactureForm />} />
            <Route path="/factures/edit/:id" element={<FactureForm />} />
            <Route path="/factures/:id" element={<FactureDetail />} />
            <Route path="/factures/print/:id" element={<FacturePrint />} />

            <Route path="/fiches" element={<FicheDePaieList />} />
            <Route path="/fiches/create" element={<FicheDePaieForm />} />
            <Route path="/fiches/edit/:id" element={<FicheDePaieForm />} />
            <Route path="/fiches/:id" element={<FicheDePaieDetail />} />

            <Route path="/article-contrat-travail" element={<ArticleContratTravailList />} />
            <Route path="/article-contrat-travail/create" element={<ArticleContratTravailForm />} />
            <Route path="/article-contrat-travail/edit/:id" element={<ArticleContratTravailForm />} />
            <Route path="/article-contrat-travail/:id" element={<ArticleContratTravailView />} />

            <Route path="/contrats/create" element={<CreateContrat />} />
            <Route path="/contrats/edit/:id" element={<EditContrat />} />
            <Route path="/contrats/:id" element={<ContratDetail />} />
            <Route path="/contrats" element={<ContratList />} />

            {}
            <Route path="/lignes-cotisation" element={<LigneCotisationList />} />
            <Route path="/lignes-cotisation/create" element={<LigneCotisationForm />} />
            <Route path="/lignes-cotisation/edit/:id" element={<LigneCotisationForm />} />
            <Route path="/lignes-cotisation/:id" element={<LigneCotisationDetail />} />

            <Route path="/tarifs" element={<TarifMissionList />} />
            <Route path="/tarifs/create" element={<TarifMissionForm />} />
            <Route path="/tarifs/edit/:id" element={<TarifMissionForm />} />
            <Route path="/tarifs/:id" element={<TarifMissionDetail />} />
          </Route>
        </Route>

        {}
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}