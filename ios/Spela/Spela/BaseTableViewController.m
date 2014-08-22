//
//  BaseTableViewController.m
//  Spela
//
//  Created by Daniel Thengvall on 8/21/14.
//  Copyright (c) 2014 Daniel Thengvall. All rights reserved.
//

#import "BaseTableViewController.h"

@implementation BaseTableViewController

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 0;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell" forIndexPath:indexPath];
    return cell;
}

@end
