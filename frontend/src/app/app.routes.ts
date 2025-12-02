import { Routes } from '@angular/router';
import { TarefaListComponent } from './components/tarefa-list/tarefa-list.component';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' }, 
  

  { path: 'home', component: TarefaListComponent },    
  
  { path: '**', redirectTo: 'home' }                   
];