import { Routes } from '@angular/router';
import { Dashboard } from './components/dashboard/dashboard';
import { ProvasSalvas } from './pages/provas-salvas/provas-salvas';
import { DetalheProva } from './pages/detalhe-prova/detalhe-prova';
import { Alimentacao } from './pages/alimentacao/alimentacao';
import { GeradorManual } from './pages/gerador-prova/gerador-manual/gerador-manual';
import { GeradorAutomatico } from './pages/gerador-prova/gerador-automatico/gerador-automatico';
import { GeradorProva } from './pages/gerador-prova/gerador-prova';

export const routes: Routes = [
   {
    path: '', 
    component: Dashboard,
    title: 'Início - Gerador de Provas'
  },
  {
    path:'gerar-prova', 
    component: GeradorProva,
    title: 'Gerador de Prova'
  },
  {
    
    path: 'gerar-prova/automatico', 
    component: GeradorAutomatico,
    title: 'Gerar Nova Prova'
  },
  {
    path: 'gerar-prova/manual',
    component: GeradorManual,
    title: 'Gerador de Prova '
  },
  {
    path: 'provas-salvas',
    component: ProvasSalvas,
    title: 'Provas Salvas'
  },
  {
    path: 'provas-salvas/:id', 
    component: DetalheProva,
    title: 'Detalhe da Prova'
  },
  {
    path:'alimentacao',
    component: Alimentacao,
    title:'Alimentacao - RAG'
  }
];
