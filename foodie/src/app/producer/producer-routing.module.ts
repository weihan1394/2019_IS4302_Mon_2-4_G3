import { AuthGuard } from './../_guards/auth-guard';
import { LayoutComponent } from './../layout/layout.component';
import { ProducerComponent } from './producer.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

const routes: Routes = [
  {
    path: '', component: LayoutComponent, canActivate: [AuthGuard], canActivateChild: [AuthGuard], children: [
      { path: 'producer', component: ProducerComponent }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProducerRoutingModule { }
